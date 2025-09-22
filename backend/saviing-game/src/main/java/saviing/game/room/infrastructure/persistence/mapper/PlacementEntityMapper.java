package saviing.game.room.infrastructure.persistence.mapper;

import java.util.Objects;

import org.springframework.stereotype.Component;
import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.domain.model.vo.ItemSize;
import saviing.game.room.domain.model.vo.PlacementId;
import saviing.game.room.domain.model.vo.Position;
import saviing.game.room.domain.model.vo.RoomId;
import saviing.game.room.infrastructure.persistence.entity.PlacementEntity;

/**
 * Placement 도메인과 JPA 엔티티 간 매핑을 담당합니다.
 */
@Component
public class PlacementEntityMapper {

    /**
     * 엔티티를 도메인 객체로 변환합니다.
     *
     * @param entity 변환할 엔티티
     * @return 변환된 도메인 객체
     * @throws IllegalArgumentException entity가 null인 경우
     */
    public Placement toDomain(PlacementEntity entity) {
        Objects.requireNonNull(entity, "entity");

        Long placementId = entity.getPlacementId();
        if (placementId == null) {
            throw new IllegalStateException("저장되지 않은 배치 엔티티는 도메인으로 변환할 수 없습니다");
        }

        return Placement.restore(
            new PlacementId(placementId),
            new RoomId(entity.getRoomId()),
            entity.getInventoryItemId(),
            new Position(entity.getPositionX(), entity.getPositionY()),
            new ItemSize(entity.getXLength(), entity.getYLength()),
            entity.getCategory(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * 도메인 객체를 엔티티로 변환합니다.
     *
     * @param placement 변환할 도메인 객체
     * @return 변환된 엔티티
     * @throws IllegalArgumentException placement가 null인 경우
     */
    public PlacementEntity toEntity(Placement placement) {
        Objects.requireNonNull(placement, "placement");

        return PlacementEntity.builder()
            .placementId(placement.getPlacementId() != null ? placement.getPlacementId().value() : null)
            .roomId(placement.getRoomId().value())
            .inventoryItemId(placement.getInventoryItemId())
            .positionX(placement.getPosition().x())
            .positionY(placement.getPosition().y())
            .xLength(placement.getSize().xLength())
            .yLength(placement.getSize().yLength())
            .category(placement.getCategory())
            .createdAt(placement.getCreatedAt())
            .updatedAt(placement.getUpdatedAt())
            .build();
    }
}
