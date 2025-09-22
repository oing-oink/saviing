package saviing.game.inventory.application.dto.result;

import lombok.Builder;

import java.util.List;

/**
 * 인벤토리 목록 조회 결과 Result입니다.
 *
 * @param inventories 인벤토리 목록
 */
@Builder
public record InventoryListResult(
    List<InventoryResult> inventories
) {
    /**
     * InventoryListResult를 생성합니다.
     *
     * @param inventories 인벤토리 목록
     * @return InventoryListResult
     */
    public static InventoryListResult of(List<InventoryResult> inventories) {
        return new InventoryListResult(inventories);
    }
}