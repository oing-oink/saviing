package saviing.game.inventory.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 인벤토리 목록 조회 응답 DTO입니다.
 */
@Schema(description = "인벤토리 목록 응답")
public record InventoryListResponse(
    @Schema(description = "인벤토리 아이템 목록")
    List<InventoryItemResponse> inventories
) {
}