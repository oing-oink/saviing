package saviing.game.room.application.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 방의 배치를 저장
     * 기존 배치를 완전히 교체하는 방식으로 동작하며,
     * 애플리케이션 서비스에서 DTO 변환과 도메인 검증을 분리하여 처리
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

        // 3. 기존 배치 조회 또는 새로 생성
        Placement placement = placementRepository.findByRoomId(roomId)
            .orElse(Placement.create(roomId));

        // 4. 도메인 로직 실행 (애그리거트가 모든 검증 담당)
        placement.replaceAllItems(placedItems);

        // 5. 저장
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
}