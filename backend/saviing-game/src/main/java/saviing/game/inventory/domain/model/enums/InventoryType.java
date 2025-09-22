package saviing.game.inventory.domain.model.enums;

import saviing.game.item.domain.model.enums.ItemType;

/**
 * 인벤토리 타입 열거형
 * ItemType과 매핑되는 인벤토리 분류를 정의합니다.
 */
public enum InventoryType {
    PET(ItemType.PET),
    ACCESSORY(ItemType.ACCESSORY),
    DECORATION(ItemType.DECORATION),
    CONSUMPTION(ItemType.CONSUMPTION);

    private final ItemType itemType;

    InventoryType(ItemType itemType) {
        this.itemType = itemType;
    }

    /**
     * 대응되는 ItemType을 반환합니다.
     *
     * @return 매핑된 ItemType
     */
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * ItemType으로부터 InventoryType을 찾습니다.
     *
     * @param itemType ItemType
     * @return 대응되는 InventoryType
     * @throws IllegalArgumentException 지원하지 않는 ItemType인 경우
     */
    public static InventoryType fromItemType(ItemType itemType) {
        if (itemType == null) {
            throw new IllegalArgumentException("ItemType은 null일 수 없습니다");
        }

        for (InventoryType inventoryType : values()) {
            if (inventoryType.itemType == itemType) {
                return inventoryType;
            }
        }

        throw new IllegalArgumentException("지원하지 않는 ItemType입니다: " + itemType);
    }

    /**
     * 주어진 ItemType이 이 InventoryType과 일치하는지 확인합니다.
     *
     * @param itemType 확인할 ItemType
     * @return 일치 여부
     */
    public boolean matches(ItemType itemType) {
        return this.itemType == itemType;
    }
}