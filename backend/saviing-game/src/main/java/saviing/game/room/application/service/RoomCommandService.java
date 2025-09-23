package saviing.game.room.application.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import saviing.game.inventory.application.service.InventoryCommandService;
import saviing.game.room.application.dto.command.CreateRoomCommand;
import saviing.game.room.application.dto.command.SaveRoomPlacementsCommand;
import saviing.game.room.application.dto.result.RoomCreatedResult;
import saviing.game.room.domain.model.aggregate.PlacedItem;
import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.domain.model.aggregate.Room;
import saviing.game.room.application.dto.command.PlaceItemCommand;
import saviing.game.room.domain.model.vo.ItemSize;
import saviing.game.room.domain.model.vo.Position;
import saviing.game.room.domain.model.vo.RoomId;
import saviing.game.room.domain.model.vo.RoomNumber;
import saviing.game.room.domain.repository.PlacementRepository;
import saviing.game.room.domain.repository.RoomRepository;

/**
 * Room 도메인의 명령 처리를 담당하는 애플리케이션 서비스
 * 방 생성, 배치 저장 등의 비즈니스 유스케이스를 구현하며,
 * DTO와 도메인 객체 간의 변환 및 트랜잭션 경계를 관리
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoomCommandService {

    private final PlacementRepository placementRepository;
    private final RoomRepository roomRepository;
    private final InventoryCommandService inventoryCommandService;

    /**
     * 방의 배치를 저장
     * 기존 배치를 완전히 교체하는 방식으로 동작하며,
     * Inventory BC와 동기적으로 동기화하여 강한 일관성을 보장
     *
     * @param command 방 배치 저장 명령 (roomId, characterId, placedItems 포함)
     * @throws IllegalArgumentException command가 null이거나 유효하지 않은 경우
     * @throws IllegalArgumentException 배치 규칙을 위반하는 경우 (겹침, 펫 개수 초과 등)
     */
    public void savePlacements(@NonNull SaveRoomPlacementsCommand command) {
        // 1. 명령 검증
        command.validate();

        RoomId roomId = new RoomId(command.roomId());

        // 2. DTO → 도메인 객체 변환 (애플리케이션 서비스에서 직접 처리)
        List<PlacedItem> placedItems = convertToPlacedItems(command.placedItems());

        // 3. Inventory 상태 초기화 (해당 방에 배치된 모든 데코레이션을 미사용 상태로)
        inventoryCommandService.resetRoomUsage(command.roomId());

        // 4. 새 배치 아이템들을 사용중 상태로 마킹
        List<Long> inventoryItemIds = extractInventoryItemIds(command.placedItems());
        inventoryCommandService.markAsUsed(inventoryItemIds, command.roomId());

        // 5. 기존 배치 조회 또는 새로 생성
        Placement placement = placementRepository.findByRoomId(roomId)
            .orElse(Placement.create(roomId));

        // 6. 도메인 로직 실행 (애그리거트가 모든 검증 담당)
        placement.replaceAllItems(placedItems);

        // 7. 저장
        placementRepository.save(placement);
    }

    /**
     * PlaceItemCommand 목록을 PlacedItem 목록으로 변환
     * 애플리케이션 서비스에서 DTO와 도메인 객체 간의 변환을 직접 처리
     *
     * @param commands 변환할 PlaceItemCommand 목록
     * @return 변환된 PlacedItem 목록
     * @throws IllegalArgumentException commands가 null이거나 잘못된 값이 포함된 경우
     */
    private List<PlacedItem> convertToPlacedItems(@NonNull List<PlaceItemCommand> commands) {
        return commands.stream()
            .map(command -> PlacedItem.create(
                command.inventoryItemId(),
                command.itemId(),
                new Position(command.positionX(), command.positionY()),
                new ItemSize(command.xLength(), command.yLength()),
                command.category()
            ))
            .collect(Collectors.toList());
    }

    /**
     * 새로운 방을 생성
     * 캐릭터와 방 번호를 기반으로 새로운 방을 생성하며,
     * 중복 방 생성을 방지하기 위한 검증을 수행한다.
     *
     * @param command 방 생성 명령 (characterId, roomNumber 포함)
     * @return 생성된 방의 정보를 담은 결과 객체
     * @throws IllegalArgumentException command가 null이거나 유효하지 않은 경우
     * @throws IllegalStateException 동일한 캐릭터와 방 번호로 방이 이미 존재하는 경우
     */
    public RoomCreatedResult createRoom(@NonNull CreateRoomCommand command) {
        // 1. 명령 검증
        command.validate();

        // 2. 중복 방 생성 검증
        validateDuplicateRoom(command.characterId(), command.roomNumber());

        // 3. Room 도메인 객체 생성
        Room room = Room.create(command.characterId(), command.roomNumber());

        // 4. 방 저장
        Room savedRoom = roomRepository.save(room);

        // 5. 결과 변환 및 반환
        return RoomCreatedResult.from(savedRoom);
    }

    /**
     * PlaceItemCommand 목록에서 인벤토리 아이템 ID 목록을 추출
     * Inventory BC와의 동기화를 위해 사용되는 Helper 메서드
     *
     * @param commands 인벤토리 아이템 ID를 추출할 PlaceItemCommand 목록
     * @return 추출된 인벤토리 아이템 ID 목록
     * @throws IllegalArgumentException commands가 null인 경우
     */
    private List<Long> extractInventoryItemIds(@NonNull List<PlaceItemCommand> commands) {
        return commands.stream()
            .map(PlaceItemCommand::inventoryItemId)
            .collect(Collectors.toList());
    }

    /**
     * 중복 방 생성 여부를 검증
     * 동일한 캐릭터와 방 번호 조합으로 방이 이미 존재하는지 확인한다.
     *
     * @param characterId 캐릭터 식별자
     * @param roomNumber 방 번호
     * @throws IllegalStateException 중복되는 방이 이미 존재하는 경우
     */
    private void validateDuplicateRoom(Long characterId, RoomNumber roomNumber) {
        roomRepository.findByCharacterIdAndRoomNumber(characterId, roomNumber)
            .ifPresent(existingRoom -> {
                throw new IllegalStateException(
                    String.format("캐릭터 %d의 %d번 방이 이미 존재합니다",
                        characterId, roomNumber.value())
                );
            });
    }


}