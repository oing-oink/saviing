package saviing.game.inventory.domain.model.aggregate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.event.InventoryItemAddedEvent;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.item.domain.model.enums.Accessory;
import saviing.game.item.domain.model.vo.ItemId;

import java.time.LocalDateTime;

/**
 * 액세서리 인벤토리 Aggregate.
 * 액세서리 아이템의 사용 여부만 관리합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessoryInventory extends Inventory {
    private Accessory category;
    private InventoryItemId petInventoryItemId;

    /**
     * AccessoryInventory 생성자(Builder 패턴 사용).
     */
    @Builder
    private AccessoryInventory(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        ItemId itemId,
        boolean isUsed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Accessory category,
        InventoryItemId petInventoryItemId
    ) {
        super(inventoryItemId, characterId, itemId, InventoryType.ACCESSORY, isUsed, createdAt, updatedAt);
        this.category = category;
        this.petInventoryItemId = petInventoryItemId;

        validateAccessorySpecificInvariants();
    }

    /**
     * 신규 액세서리 인벤토리를 생성합니다.
     */
    public static AccessoryInventory create(
        CharacterId characterId,
        ItemId itemId,
        Accessory accessoryCategory
    ) {
        validateAccessoryCategory(accessoryCategory);

        AccessoryInventory accessoryInventory = AccessoryInventory.builder()
            .characterId(characterId)
            .itemId(itemId)
            .category(accessoryCategory)
            .isUsed(false)
            .build();

        accessoryInventory.addDomainEvent(InventoryItemAddedEvent.of(
            accessoryInventory.getInventoryItemId(),
            characterId,
            itemId,
            InventoryType.ACCESSORY
        ));

        return accessoryInventory;
    }



    /**
     * 액세서리 카테고리가 일치하는지 확인합니다.
     */
    public boolean isCategory(Accessory accessoryCategory) {
        return this.category == accessoryCategory;
    }

    /**
     * 펫에게 액세서리를 장착합니다.
     */
    public void equipToPet(InventoryItemId petInventoryItemId) {
        if (this.isUsed) {
            throw new IllegalStateException("이미 장착된 액세서리입니다");
        }
        if (petInventoryItemId == null) {
            throw new IllegalArgumentException("펫 인벤토리 ID는 null일 수 없습니다");
        }
        this.petInventoryItemId = petInventoryItemId;
        use();
    }

    /**
     * 펫에서 액세서리를 해제합니다.
     */
    public void unequipFromPet() {
        if (!this.isUsed) {
            throw new IllegalStateException("장착되지 않은 액세서리입니다");
        }
        this.petInventoryItemId = null;
        unuse();
    }

    /**
     * 특정 펫에게 장착되어 있는지 확인합니다.
     */
    public boolean isEquippedToPet(InventoryItemId petInventoryItemId) {
        return this.isUsed && this.petInventoryItemId != null && this.petInventoryItemId.equals(petInventoryItemId);
    }

    private static void validateAccessoryCategory(Accessory accessoryCategory) {
        if (accessoryCategory == null) {
            throw new IllegalArgumentException("액세서리 카테고리는 null일 수 없습니다");
        }
        // 현재는 HAT만 지원합니다.
        if (accessoryCategory != Accessory.HAT) {
            throw new IllegalArgumentException("지원하지 않는 액세서리 카테고리입니다: " + accessoryCategory);
        }
    }

    private void validateAccessorySpecificInvariants() {
        if (category == null) {
            throw new IllegalArgumentException("액세서리 카테고리는 null일 수 없습니다");
        }
    }

}