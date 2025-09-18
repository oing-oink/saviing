package saviing.bank.account.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import saviing.bank.account.application.port.in.result.ProductSummaryResult;

/**
 * 상품 목록 조회 응답 DTO입니다.
 */
public record ProductSummaryResponse(
    @Schema(description = "상품 ID", example = "1")
    Long productId,
    @Schema(description = "상품명", example = "자유입출금통장")
    String productName,
    @Schema(description = "상품 코드", example = "FREE_CHECKING")
    String productCode,
    @Schema(description = "상품 카테고리", example = "DEMAND_DEPOSIT")
    String productCategory,
    @Schema(description = "상품 설명", example = "언제든지 자유롭게 입출금이 가능한 통장")
    String description,
    @Schema(description = "최소 금리 (베이시스 포인트)", example = "0")
    Short minInterestRateBps,
    @Schema(description = "최대 금리 (베이시스 포인트)", example = "450")
    Short maxInterestRateBps
) {

    public static ProductSummaryResponse from(ProductSummaryResult result) {
        return new ProductSummaryResponse(
            result.productId(),
            result.productName(),
            result.productCode(),
            result.productCategory(),
            result.description(),
            result.minInterestRateBps(),
            result.maxInterestRateBps()
        );
    }
}
