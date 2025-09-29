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
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.enums.ItemCategory;
import saviing.game.inventory.presentation.dto.response.InventoryItemResponse;
import saviing.game.inventory.presentation.dto.response.InventoryListResponse;

@Tag(name = "Inventory", description = "인벤토리 관리 API")
public interface InventoryApi {

    @Operation(
        summary = "캐릭터 인벤토리 조회",
        description = "필터링 조건을 사용하여 캐릭터가 보유한 인벤토리 아이템을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터 (캐릭터 ID, 인벤토리 타입, 카테고리 값 오류)",
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
        @Parameter(description = "캐릭터 ID", example = "1")
        @PathVariable Long characterId,

        @Parameter(description = "인벤토리 타입 (PET, ACCESSORY, DECORATION, CONSUMPTION)", example = "PET")
        InventoryType type,

        @Parameter(description = "아이템 카테고리 (CAT, HAT, LEFT, RIGHT, BOTTOM, ROOM_COLOR, TOY, FOOD)", example = "HAT")
        ItemCategory category,

        @Parameter(description = "사용 여부 (true: 사용중, false: 미사용)", example = "false")
        Boolean isUsed
    );

    @Operation(
        summary = "단일 인벤토리 아이템 조회",
        description = "인벤토리 아이템 ID로 특정 인벤토리 아이템의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 인벤토리 아이템 ID 형식 - 양수가 아닌 값 입력",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
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
        @Parameter(description = "인벤토리 아이템 ID", example = "1")
        @PathVariable Long inventoryItemId
    );
}