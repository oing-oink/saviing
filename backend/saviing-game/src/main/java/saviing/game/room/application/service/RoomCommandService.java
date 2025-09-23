package saviing.game.room.application.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import saviing.game.inventory.application.service.InventoryCommandService;
import saviing.game.room.application.dto.command.SaveRoomPlacementsCommand;
import saviing.game.room.domain.model.aggregate.PlacedItem;
import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.application.dto.command.PlaceItemCommand;
import saviing.game.room.domain.model.vo.ItemSize;
import saviing.game.room.domain.model.vo.Position;
import saviing.game.room.domain.model.vo.RoomId;
import saviing.game.room.domain.repository.PlacementRepository;

/**
 * Room 도메인의 명령 처리를 담당하는 애플리케이션 서비스
 * 배치 저장 등의 비즈니스 유스케이스를 구현하며,
 * DTO와 도메인 객체 간의 변환 및 트랜잭션 경계를 관리
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoomCommandService {

    private final PlacementRepository placementRepository;
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
                new Position(command.positionX(), command.positionY()),
                new ItemSize(command.xLength(), command.yLength()),
                command.category()
            ))
            .collect(Collectors.toList());
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

}