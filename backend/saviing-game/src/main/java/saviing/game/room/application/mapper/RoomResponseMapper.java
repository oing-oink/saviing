package saviing.game.room.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import org.springframework.stereotype.Component;

import saviing.game.room.application.dto.result.RoomPlacementListResult;
import saviing.game.room.application.dto.result.RoomResult;
import saviing.game.room.domain.model.aggregate.PlacedItem;

/**
 * 도메인 객체를 응답 DTO로 변환하는 매퍼
 * PlacedItem 목록 및 Room 정보를 클라이언트가 사용할 수 있는 응답 형식으로 변환
 */
@Component
public class RoomResponseMapper {

    /**
     * PlacedItem 목록을 RoomPlacementListResult로 변환
     * 새로운 애그리거트 구조에 맞춰 PlacedItem 리스트를 응답 DTO로 변환
     *
     * @param roomId 방 식별자
     * @param placedItems 배치된 아이템 도메인 객체 목록
     * @return 변환된 응답 DTO (PlacementInfo 목록 포함)
     * @throws IllegalArgumentException roomId가 null이거나 placedItems가 null인 경우
     */
    public RoomPlacementListResult toRoomPlacementListResult(@NonNull Long roomId, @NonNull List<PlacedItem> placedItems) {
        List<RoomPlacementListResult.PlacementInfo> placementInfos = placedItems.stream()
            .map(this::toPlacementInfo)
            .collect(Collectors.toList());

        return RoomPlacementListResult.builder()
            .roomId(roomId)
            .placements(placementInfos)
            .build();
    }

    /**
     * PlacedItem 도메인 객체를 PlacementInfo DTO로 변환
     * 개별 배치 아이템의 정보를 클라이언트 응답 형식으로 변환
     *
     * @param placedItem 배치된 아이템 도메인 객체
     * @return 변환된 PlacementInfo DTO
     * @throws IllegalArgumentException placedItem이 null인 경우
     */
    private RoomPlacementListResult.PlacementInfo toPlacementInfo(@NonNull PlacedItem placedItem) {
        return RoomPlacementListResult.PlacementInfo.builder()
            .inventoryItemId(placedItem.getInventoryItemId())
            .positionX(placedItem.getPosition().x())
            .positionY(placedItem.getPosition().y())
            .xLength(placedItem.getSize().xLength())
            .yLength(placedItem.getSize().yLength())
            .category(placedItem.getCategory().name())
            .build();
    }

    /**
     * RoomResult를 RoomResult 그대로 반환 (추가 변환 없음)
     * Application layer의 Result DTO를 그대로 사용
     *
     * @param roomResult 방 조회 결과 DTO
     * @return 동일한 RoomResult 인스턴스
     * @throws IllegalArgumentException roomResult가 null인 경우
     */
    public RoomResult toRoomResult(@NonNull RoomResult roomResult) {
        return roomResult;
    }
}