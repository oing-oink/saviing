package saviing.game.inventory.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 소모품 인벤토리 JPA Entity
 */
@Entity
@Table(name = "consumption_inventory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsumptionInventoryEntity extends InventoryEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ConsumptionCategoryEntity category;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Builder
    public ConsumptionInventoryEntity(
        Long inventoryItemId,
        Long characterId,
        Long itemId,
        Boolean isUsed,
        java.time.LocalDateTime createdAt,
        java.time.LocalDateTime updatedAt,
        ConsumptionCategoryEntity category,
        Integer count
    ) {
        super(inventoryItemId, characterId, itemId, InventoryTypeEntity.CONSUMPTION, isUsed, createdAt, updatedAt);
        this.category = category;
        this.count = count;
    }

    /**
     * 소모품 카테고리 Entity 열거형
     */
    public enum ConsumptionCategoryEntity {
        TOY,
        FOOD
    }
}