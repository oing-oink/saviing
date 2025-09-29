package saviing.game.inventory.domain.exception;

import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 인벤토리 아이템을 찾을 수 없을 때 발생하는 예외
 */
public class InventoryItemNotFoundException extends InventoryException {

    public InventoryItemNotFoundException(InventoryItemId inventoryItemId) {
        super(
            InventoryErrorCode.INVENTORY_ITEM_NOT_FOUND,
            "인벤토리 아이템을 찾을 수 없습니다: " + inventoryItemId
        );
    }

    public InventoryItemNotFoundException(Long inventoryItemId) {
        super(
            InventoryErrorCode.INVENTORY_ITEM_NOT_FOUND,
            "인벤토리 아이템을 찾을 수 없습니다: " + inventoryItemId
        );
    }

    /**
     * 인벤토리 아이템을 찾을 수 없는 예외를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @return InventoryItemNotFoundException 인스턴스
     */
    public static InventoryItemNotFoundException of(InventoryItemId inventoryItemId) {
        return new InventoryItemNotFoundException(inventoryItemId);
    }

    /**
     * 인벤토리 아이템을 찾을 수 없는 예외를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID (Long)
     * @return InventoryItemNotFoundException 인스턴스
     */
    public static InventoryItemNotFoundException of(Long inventoryItemId) {
        return new InventoryItemNotFoundException(inventoryItemId);
    }
}