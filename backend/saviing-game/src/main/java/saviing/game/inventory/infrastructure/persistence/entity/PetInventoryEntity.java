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
 * 펫 인벤토리 JPA 엔티티
 * pet_inventory 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "pet_inventory")
@PrimaryKeyJoinColumn(name = "inventory_item_id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetInventoryEntity extends InventoryEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private PetCategoryEntity category;

    @Column(name = "room_id", nullable = false)
    private Long roomId;


    @Builder
    public PetInventoryEntity(
        Long inventoryItemId,
        Long characterId,
        Long itemId,
        Boolean isUsed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        PetCategoryEntity category,
        Long roomId
    ) {
        super(inventoryItemId, characterId, itemId, InventoryTypeEntity.PET, isUsed, createdAt, updatedAt);
        this.category = category;
        this.roomId = roomId;
    }

    /**
     * 펫을 업데이트합니다.
     *
     * @param category 펫 카테고리
     * @param roomId 방 ID
     */
    public void updatePet(PetCategoryEntity category, Long roomId) {
        this.category = category;
        this.roomId = roomId;
    }

    /**
     * 펫을 다른 방으로 이동합니다.
     *
     * @param roomId 이동할 방 ID
     */
    public void moveToRoom(Long roomId) {
        this.roomId = roomId;
    }

    /**
     * 펫 카테고리 열거형
     */
    public enum PetCategoryEntity {
        CAT
    }
}