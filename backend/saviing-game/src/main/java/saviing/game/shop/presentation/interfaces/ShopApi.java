package saviing.game.shop.presentation.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import saviing.common.response.ApiResult;
import saviing.common.response.ErrorResult;
import saviing.game.shop.presentation.dto.request.PurchaseItemRequest;
import saviing.game.shop.presentation.dto.request.GachaDrawRequest;
import saviing.game.shop.presentation.dto.response.PurchaseResponse;
import saviing.game.shop.presentation.dto.response.GachaInfoResponse;

/**
 * Shop API 인터페이스.
 */
@Tag(name = "Shop", description = "상점 구매 API")
public interface ShopApi {

    @Operation(
        summary = "아이템 구매",
        description = "캐릭터가 지정된 결제 수단으로 아이템을 구매합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "구매 요청 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 구매 조건 불충족",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = {
                    @ExampleObject(
                        name = "PURCHASE_ITEM_UNAVAILABLE",
                        summary = "판매 중단 아이템 구매 시",
                        value = """
                            {
                              \"success\": false,
                              \"status\": 400,
                              \"code\": \"PURCHASE_ITEM_UNAVAILABLE\",
                              \"message\": \"판매 중단된 아이템입니다\",
                              \"timestamp\": \"2025-01-15T10:30:00+09:00[Asia/Seoul]\"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "PURCHASE_INSUFFICIENT_FUNDS",
                        summary = "보유 재화가 부족한 경우",
                        value = """
                            {
                              \"success\": false,
                              \"status\": 400,
                              \"code\": \"PURCHASE_INSUFFICIENT_FUNDS\",
                              \"message\": \"잔액이 부족합니다\",
                              \"timestamp\": \"2025-01-15T10:30:00+09:00[Asia/Seoul]\"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "INVALID_INPUT_VALUE",
                        summary = "요청 본문 검증 실패 (공통)",
                        value = """
                            {
                              \"success\": false,
                              \"status\": 400,
                              \"code\": \"INVALID_INPUT_VALUE\",
                              \"message\": \"입력값이 올바르지 않습니다.\",
                              \"timestamp\": \"2025-01-15T10:30:00+09:00[Asia/Seoul]\",
                              \"invalidParams\": [
                                {
                                  \"field\": \"characterId\",
                                  \"message\": \"양수 값을 입력해주세요.\",
                                  \"rejectedValue\": -1
                                }
                              ]
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "요청한 자원을 찾을 수 없음",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "PURCHASE_ITEM_NOT_FOUND",
                    summary = "존재하지 않는 아이템",
                    value = """
                        {
                          \"success\": false,
                          \"status\": 404,
                          \"code\": \"PURCHASE_ITEM_NOT_FOUND\",
                          \"message\": \"구매하려는 아이템을 찾을 수 없습니다\",
                          \"timestamp\": \"2025-01-15T10:30:00+09:00[Asia/Seoul]\"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 처리 중 오류 발생",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = {
                    @ExampleObject(
                        name = "PURCHASE_PROCESSING_FAILED",
                        summary = "동기 처리 중 예기치 못한 예외",
                        value = """
                            {
                              \"success\": false,
                              \"status\": 500,
                              \"code\": \"PURCHASE_PROCESSING_FAILED\",
                              \"message\": \"구매 처리 중 오류가 발생했습니다\",
                              \"timestamp\": \"2025-01-15T10:30:00+09:00[Asia/Seoul]\"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "INTERNAL_SERVER_ERROR",
                        summary = "공통 서버 오류",
                        value = """
                            {
                              \"success\": false,
                              \"status\": 500,
                              \"code\": \"INTERNAL_SERVER_ERROR\",
                              \"message\": \"서버 내부 오류가 발생했습니다.\",
                              \"timestamp\": \"2025-01-15T10:30:00+09:00[Asia/Seoul]\"
                            }
                            """
                    )
                }
            )
        )
    })
    @PostMapping("/purchase")
    ApiResult<PurchaseResponse> purchaseItem(
        @RequestBody(
            description = "구매 요청 본문",
            required = true,
            content = @Content(
                schema = @Schema(implementation = PurchaseItemRequest.class),
                examples = @ExampleObject(
                    name = "PurchaseItemRequest",
                    summary = "코인으로 아이템 구매",
                    value = """
                        {
                          \"characterId\": 1001,
                          \"itemId\": 501,
                          \"paymentMethod\": \"COIN\"
                        }
                        """
                )
            )
        )
        @org.springframework.web.bind.annotation.RequestBody PurchaseItemRequest request
    );

    @Operation(
        summary = "가챠 정보 조회",
        description = "가챠풀의 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "가챠 정보 조회 성공")
    })
    @GetMapping("/gacha/info")
    ApiResult<GachaInfoResponse> getGachaInfo();

    @Operation(
        summary = "가챠 뽑기",
        description = "가챠를 뽑아 랜덤 아이템을 획득합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "가챠 뽑기 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 뽑기 조건 불충족",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = {
                    @ExampleObject(
                        name = "PURCHASE_INSUFFICIENT_FUNDS",
                        summary = "보유 재화가 부족한 경우",
                        value = """
                            {
                              \"success\": false,
                              \"status\": 400,
                              \"code\": \"PURCHASE_INSUFFICIENT_FUNDS\",
                              \"message\": \"잔액이 부족합니다\",
                              \"timestamp\": \"2025-01-15T10:30:00+09:00[Asia/Seoul]\"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "PURCHASE_INVALID_PAYMENT_METHOD",
                        summary = "지원하지 않는 결제 수단",
                        value = """
                            {
                              \"success\": false,
                              \"status\": 400,
                              \"code\": \"PURCHASE_INVALID_PAYMENT_METHOD\",
                              \"message\": \"지원하지 않는 결제 수단입니다\",
                              \"timestamp\": \"2025-01-15T10:30:00+09:00[Asia/Seoul]\"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "INVALID_INPUT_VALUE",
                        summary = "요청 본문 검증 실패 (공통)",
                        value = """
                            {
                              \"success\": false,
                              \"status\": 400,
                              \"code\": \"INVALID_INPUT_VALUE\",
                              \"message\": \"입력값이 올바르지 않습니다.\",
                              \"timestamp\": \"2025-01-15T10:30:00+09:00[Asia/Seoul]\",
                              \"invalidParams\": [
                                {
                                  \"field\": \"gachaPoolId\",
                                  \"message\": \"양수 값을 입력해주세요.\",
                                  \"rejectedValue\": 0
                                }
                              ]
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 처리 중 오류 발생",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = {
                    @ExampleObject(
                        name = "PURCHASE_PROCESSING_FAILED",
                        summary = "가챠 처리 중 예기치 못한 예외",
                        value = """
                            {
                              \"success\": false,
                              \"status\": 500,
                              \"code\": \"PURCHASE_PROCESSING_FAILED\",
                              \"message\": \"구매 처리 중 오류가 발생했습니다\",
                              \"timestamp\": \"2025-01-15T10:30:00+09:00[Asia/Seoul]\"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "INTERNAL_SERVER_ERROR",
                        summary = "공통 서버 오류",
                        value = """
                            {
                              \"success\": false,
                              \"status\": 500,
                              \"code\": \"INTERNAL_SERVER_ERROR\",
                              \"message\": \"서버 내부 오류가 발생했습니다.\",
                              \"timestamp\": \"2025-01-15T10:30:00+09:00[Asia/Seoul]\"
                            }
                            """
                    )
                }
            )
        )
    })
    @PostMapping("/gacha/draw")
    ApiResult<PurchaseResponse> drawGacha(
        @RequestBody(
            description = "가챠 뽑기 요청 본문",
            required = true,
            content = @Content(
                schema = @Schema(implementation = GachaDrawRequest.class),
                examples = @ExampleObject(
                    name = "GachaDrawRequest",
                    summary = "코인으로 가챠 뽑기",
                    value = """
                        {
                          \"characterId\": 1001,
                          \"gachaPoolId\": 1,
                          \"paymentMethod\": \"COIN\"
                        }
                        """
                )
            )
        )
        @org.springframework.web.bind.annotation.RequestBody GachaDrawRequest request
    );
}