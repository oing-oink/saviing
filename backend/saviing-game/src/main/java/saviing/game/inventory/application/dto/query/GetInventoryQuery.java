package saviing.game.inventory.application.dto.query;

import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 인벤토리 아이템 조회 Query
 */
public record GetInventoryQuery(
    InventoryItemId inventoryItemId
) {
    /**
     * GetInventoryQuery를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @return GetInventoryQuery
     */
    public static GetInventoryQuery of(Long inventoryItemId) {
        return new GetInventoryQuery(InventoryItemId.of(inventoryItemId));
    }
}