package saviing.game.inventory.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 데코레이션 인벤토리 JPA 엔티티
 * decoration_inventory 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "decoration_inventory")
@PrimaryKeyJoinColumn(name = "inventory_item_id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DecorationInventoryEntity extends InventoryEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private DecorationCategoryEntity category;

    @Builder
    public DecorationInventoryEntity(
        Long inventoryItemId,
        Long characterId,
        Long itemId,
        Boolean isUsed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        DecorationCategoryEntity category
    ) {
        super(inventoryItemId, characterId, itemId, InventoryTypeEntity.DECORATION, isUsed, createdAt, updatedAt);
        this.category = category;
    }

    /**
     * 데코레이션 정보를 업데이트합니다.
     *
     * @param category 데코레이션 카테고리
     */
    public void updateDecorationInfo(DecorationCategoryEntity category) {
        this.category = category;
    }

    /**
     * 데코레이션 카테고리 열거형
     */
    public enum DecorationCategoryEntity {
        LEFT, RIGHT, BOTTOM, ROOM_COLOR
    }
}