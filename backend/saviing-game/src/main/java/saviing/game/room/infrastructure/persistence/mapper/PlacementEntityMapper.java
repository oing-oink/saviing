package saviing.game.room.infrastructure.persistence.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.NonNull;

import saviing.game.room.domain.model.aggregate.PlacedItem;
import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.domain.model.vo.ItemSize;
import saviing.game.room.domain.model.vo.Position;
import saviing.game.room.domain.model.vo.RoomId;
import saviing.game.room.infrastructure.persistence.entity.PlacementEntity;

/**
 * PlacementEntity와 Placement 도메인 객체 간의 변환을 담당하는 매퍼
 * 영속성 계층과 도메인 계층 간의 객체 변환 역할을 수행하며,
 * 룸별로 여러 PlacementEntity를 하나의 Placement 애그리거트로 그룹화하거나 분해함
 */
public class PlacementEntityMapper {

    /**
     * PlacementEntity 목록을 Placement 도메인 객체로 변환
     * 같은 룸의 여러 PlacementEntity를 하나의 Placement 애그리거트로 그룹화
     *
     * @param entities 변환할 PlacementEntity 목록 (같은 룸에 속해야 함)
     * @return 변환된 Placement 도메인 객체 (Optional)
     * @throws IllegalArgumentException entities가 null인 경우
     * @throws IllegalArgumentException 서로 다른 룸의 엔티티가 포함된 경우
     */
    public static Optional<Placement> toDomain(@NonNull List<PlacementEntity> entities) {
        if (entities.isEmpty()) {
            return Optional.empty();
        }

        // 첫 번째 엔티티에서 룸 정보 추출
        PlacementEntity firstEntity = entities.get(0);
        RoomId roomId = new RoomId(firstEntity.getRoomId());

        // 모든 엔티티가 같은 룸인지 검증
        validateSameRoom(entities, firstEntity.getRoomId());

        // PlacedItem 목록으로 변환
        List<PlacedItem> placedItems = entities.stream()
            .map(PlacementEntityMapper::toPlacedItem)
            .collect(Collectors.toList());

        // 첫 번째 엔티티의 메타 정보로 Placement 애그리거트 복원
        return Optional.of(Placement.restore(
            roomId,
            placedItems,
            firstEntity.getCreatedAt(),
            firstEntity.getUpdatedAt()
        ));
    }

    /**
     * Placement 도메인 객체를 PlacementEntity 목록으로 변환
     * 하나의 Placement 애그리거트를 여러 PlacementEntity로 분해
     *
     * @param placement 변환할 Placement 도메인 객체
     * @return 변환된 PlacementEntity 목록 (빈 배치인 경우 빈 목록 반환)
     * @throws IllegalArgumentException placement가 null인 경우
     */
    public static List<PlacementEntity> toEntities(@NonNull Placement placement) {
        if (placement.getPlacedItems().isEmpty()) {
            return Collections.emptyList();
        }

        return placement.getPlacedItems().stream()
            .map(placedItem -> toEntity(placement, placedItem))
            .collect(Collectors.toList());
    }

    /**
     * PlacementEntity를 PlacedItem 도메인 객체로 변환
     * 개별 배치 엔티티의 아이템 정보를 도메인 객체로 변환
     *
     * @param entity 변환할 PlacementEntity
     * @return 변환된 PlacedItem 도메인 객체
     * @throws IllegalArgumentException entity가 null인 경우
     */
    private static PlacedItem toPlacedItem(@NonNull PlacementEntity entity) {
        return PlacedItem.restore(
            entity.getInventoryItemId(),
            new Position(entity.getPositionX(), entity.getPositionY()),
            new ItemSize(entity.getXLength(), entity.getYLength()),
            entity.getCategory(),
            entity.getCreatedAt()
        );
    }

    /**
     * Placement와 PlacedItem을 PlacementEntity로 변환
     * 애그리거트의 메타 정보와 개별 아이템 정보를 엔티티로 결합
     *
     * @param placement 부모 Placement 애그리거트 (메타 정보 제공)
     * @param placedItem 변환할 PlacedItem (아이템별 정보 제공)
     * @return 변환된 PlacementEntity
     * @throws IllegalArgumentException placement나 placedItem이 null인 경우
     */
    private static PlacementEntity toEntity(@NonNull Placement placement, @NonNull PlacedItem placedItem) {
        return PlacementEntity.builder()
            .placementId(null)  // DB에서 자동 생성되도록 null로 설정
            .roomId(placement.getRoomId().value())
            .inventoryItemId(placedItem.getInventoryItemId())
            .positionX(placedItem.getPosition().x())
            .positionY(placedItem.getPosition().y())
            .xLength(placedItem.getSize().xLength())
            .yLength(placedItem.getSize().yLength())
            .category(placedItem.getCategory())
            .createdAt(placement.getCreatedAt())
            .updatedAt(placement.getUpdatedAt())
            .build();
    }

    /**
     * 모든 엔티티가 같은 룸에 속하는지 검증
     * 하나의 Placement 애그리거트로 변환하기 위해 룸 일관성을 보장
     *
     * @param entities 검증할 엔티티 목록
     * @param expectedRoomId 예상되는 룸 ID
     * @throws IllegalArgumentException 다른 룸의 엔티티가 포함된 경우
     */
    private static void validateSameRoom(@NonNull List<PlacementEntity> entities, @NonNull Long expectedRoomId) {
        boolean allSameRoom = entities.stream()
            .allMatch(entity -> expectedRoomId.equals(entity.getRoomId()));

        if (!allSameRoom) {
            throw new IllegalArgumentException(
                "All placement entities must belong to the same room. Expected: " + expectedRoomId);
        }
    }
}