package saviing.game.inventory.application.dto.result;

import lombok.Builder;

/**
 * 인벤토리 아이템 추가 결과 Result입니다.
 *
 * @param inventoryItemId 추가된 인벤토리 아이템 ID
 */
@Builder
public record InventoryAddedResult(
    Long inventoryItemId
) {
    /**
     * InventoryAddedResult를 생성합니다.
     *
     * @param inventoryItemId 추가된 인벤토리 아이템 ID
     * @return InventoryAddedResult
     */
    public static InventoryAddedResult of(Long inventoryItemId) {
        return new InventoryAddedResult(inventoryItemId);
    }
}