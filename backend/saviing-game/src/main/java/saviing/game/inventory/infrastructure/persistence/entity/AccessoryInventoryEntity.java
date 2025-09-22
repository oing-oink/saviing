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
 * 액세서리 인벤토리 JPA 엔티티
 * accessory_inventory 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "accessory_inventory")
@PrimaryKeyJoinColumn(name = "inventory_item_id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessoryInventoryEntity extends InventoryEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private AccessoryCategoryEntity category;

    @Builder
    public AccessoryInventoryEntity(
        Long inventoryItemId,
        Long characterId,
        Long itemId,
        Boolean isUsed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        AccessoryCategoryEntity category
    ) {
        super(inventoryItemId, characterId, itemId, InventoryTypeEntity.ACCESSORY, isUsed, createdAt, updatedAt);
        this.category = category;
    }

    /**
     * 액세서리 정보를 업데이트합니다.
     *
     * @param category 액세서리 카테고리
     */
    public void updateAccessoryInfo(AccessoryCategoryEntity category) {
        this.category = category;
    }

    /**
     * 액세서리 카테고리 열거형
     */
    public enum AccessoryCategoryEntity {
        HAT
    }
}