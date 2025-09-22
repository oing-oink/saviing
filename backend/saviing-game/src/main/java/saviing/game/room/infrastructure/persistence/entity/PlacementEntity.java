package saviing.game.room.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import saviing.game.room.domain.model.aggregate.Category;

    /**
     * room_placement 테이블과 매핑되는 JPA 엔티티입니다.
     */
@Entity
@Table(
    name = "room_placement",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_placement_inventory_item", columnNames = "inventory_item_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlacementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "placement_id")
    private Long placementId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "inventory_item_id", nullable = false, unique = true)
    private Long inventoryItemId;

    @Column(name = "position_x", nullable = false)
    private Integer positionX;

    @Column(name = "position_y", nullable = false)
    private Integer positionY;

    @Column(name = "x_length", nullable = false)
    private Integer xLength;

    @Column(name = "y_length", nullable = false)
    private Integer yLength;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private Category category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * PlacementEntity 생성자입니다.
     *
     * @param placementId 배치 식별자
     * @param roomId 방 식별자
     * @param inventoryItemId 인벤토리 아이템 식별자
     * @param positionX X 좌표
     * @param positionY Y 좌표
     * @param xLength X축 길이
     * @param yLength Y축 길이
     * @param category 배치 카테고리
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     */
    @Builder
    private PlacementEntity(
        Long placementId,
        Long roomId,
        Long inventoryItemId,
        Integer positionX,
        Integer positionY,
        Integer xLength,
        Integer yLength,
        Category category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.placementId = placementId;
        this.roomId = roomId;
        this.inventoryItemId = inventoryItemId;
        this.positionX = positionX;
        this.positionY = positionY;
        this.xLength = xLength;
        this.yLength = yLength;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 엔티티 저장 전에 생성 및 수정 시각을 초기화합니다.
     *
     * @return 없음
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 엔티티 수정 전에 수정 시각을 갱신합니다.
     *
     * @return 없음
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
