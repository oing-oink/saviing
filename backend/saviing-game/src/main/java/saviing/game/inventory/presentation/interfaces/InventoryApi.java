package saviing.game.inventory.presentation.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import saviing.common.response.ApiResult;
import saviing.common.response.ErrorResult;
import saviing.game.inventory.presentation.dto.response.InventoryItemResponse;
import saviing.game.inventory.presentation.dto.response.InventoryListResponse;

@Tag(name = "Inventory", description = "인벤토리 조회 API")
public interface InventoryApi {

    @Operation(
        summary = "캐릭터 인벤토리 조회",
        description = "캐릭터가 보유한 전체 인벤토리 아이템을 조회합니다. 펫, 액세서리, 데코레이션, 소모품 등 모든 종류의 아이템이 포함됩니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "인벤토리 조회 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 캐릭터 ID 형식 - 양수가 아닌 값 입력",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "캐릭터를 찾을 수 없음 (CHARACTER_NOT_FOUND)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "CHARACTER_NOT_FOUND",
                    summary = "캐릭터를 찾을 수 없는 경우",
                    value = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "CHARACTER_NOT_FOUND",
                          "message": "캐릭터를 찾을 수 없습니다",
                          "timestamp": "2025-01-15T10:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        )
    })
    ApiResult<InventoryListResponse> getInventory(
        @Parameter(description = "캐릭터 ID", required = true, example = "1")
        @PathVariable Long characterId
    );

    @Operation(
        summary = "인벤토리 아이템 단건 조회",
        description = "특정 인벤토리 아이템의 상세 정보를 조회합니다. 아이템 타입에 따라 roomId, petInventoryItemId, count 등의 추가 정보가 포함될 수 있습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "인벤토리 아이템 조회 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 인벤토리 아이템 ID 형식 - 양수가 아닌 값 입력",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "인벤토리 아이템 접근 권한 없음 (INVALID_INVENTORY_OWNER)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "INVALID_INVENTORY_OWNER",
                    summary = "인벤토리 아이템의 소유자가 아닌 경우",
                    value = """
                        {
                          "success": false,
                          "status": 403,
                          "code": "INVALID_INVENTORY_OWNER",
                          "message": "인벤토리 아이템의 소유자가 아닙니다",
                          "timestamp": "2025-01-15T10:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "인벤토리 아이템을 찾을 수 없음 (INVENTORY_ITEM_NOT_FOUND)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "INVENTORY_ITEM_NOT_FOUND",
                    summary = "인벤토리 아이템을 찾을 수 없는 경우",
                    value = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "INVENTORY_ITEM_NOT_FOUND",
                          "message": "인벤토리 아이템을 찾을 수 없습니다",
                          "timestamp": "2025-01-15T10:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        )
    })
    ApiResult<InventoryItemResponse> getInventoryItem(
        @Parameter(description = "인벤토리 아이템 ID", required = true, example = "10")
        @PathVariable Long inventoryItemId
    );
}