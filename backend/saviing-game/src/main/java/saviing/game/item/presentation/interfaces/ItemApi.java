package saviing.game.item.presentation.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import saviing.common.response.ApiResult;
import saviing.common.response.ErrorResult;
import saviing.game.item.presentation.dto.response.ItemListResponse;
import saviing.game.item.presentation.dto.response.ItemResponse;

/**
 * 아이템 API 컨트롤러 인터페이스
 * OpenAPI 문서화를 위한 인터페이스입니다.
 */
@Tag(name = "Item", description = "아이템 관리 API")
public interface ItemApi {

    @Operation(
        summary = "아이템 목록 조회",
        description = "검색 조건과 정렬 옵션을 사용하여 아이템 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        )
    })
    ApiResult<ItemListResponse> getItems(
        @Parameter(description = "아이템 타입 (PET, ACCESSORY, DECORATION)", example = "PET")
        String type,

        @Parameter(description = "아이템 카테고리 (CAT, HAT, LEFT, RIGHT, BOTTOM, ROOM_COLOR)", example = "CAT")
        String category,

        @Parameter(description = "희귀도 (COMMON, RARE, EPIC, LEGENDARY)", example = "COMMON")
        String rarity,

        @Parameter(description = "아이템 이름 검색 키워드", example = "고양이")
        String keyword,

        @Parameter(description = "판매 가용성 (true: 판매중, false: 판매중단)", example = "true")
        Boolean available,

        @Parameter(description = "정렬 필드 (name, price, rarity, createdAt)", example = "price")
        String sort,

        @Parameter(description = "정렬 방향 (asc, desc)", example = "asc")
        String order,

        @Parameter(description = "가격 정렬시 코인 타입 (coin, fishCoin)", example = "coin")
        String coinType
    );

    @Operation(
        summary = "단일 아이템 조회",
        description = "아이템 ID로 특정 아이템의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 아이템 ID 형식 - 양수가 아닌 값 입력",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "아이템을 찾을 수 없음 (ITEM_NOT_FOUND)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "ITEM_NOT_FOUND",
                    summary = "아이템을 찾을 수 없는 경우",
                    value = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "ITEM_NOT_FOUND",
                          "message": "아이템을 찾을 수 없습니다",
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
    ApiResult<ItemResponse> getItem(
        @Parameter(description = "아이템 ID", example = "1")
        Long itemId
    );
}