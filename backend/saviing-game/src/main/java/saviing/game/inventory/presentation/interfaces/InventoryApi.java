package saviing.game.inventory.presentation.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import saviing.common.response.ApiResult;
import saviing.game.inventory.presentation.dto.response.InventoryItemResponse;
import saviing.game.inventory.presentation.dto.response.InventoryListResponse;

@Tag(name = "Inventory", description = "인벤토리 조회 API")
public interface InventoryApi {

    @Operation(summary = "캐릭터 인벤토리 조회", description = "캐릭터가 보유한 전체 인벤토리 아이템을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ApiResult<InventoryListResponse> getInventory(
        @Parameter(description = "캐릭터 ID", required = true, example = "1")
        @PathVariable Long characterId
    );

    @Operation(summary = "인벤토리 아이템 단건 조회", description = "특정 인벤토리 아이템의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ApiResult<InventoryItemResponse> getInventoryItem(
        @Parameter(description = "인벤토리 아이템 ID", required = true, example = "10")
        @PathVariable Long inventoryItemId
    );
}