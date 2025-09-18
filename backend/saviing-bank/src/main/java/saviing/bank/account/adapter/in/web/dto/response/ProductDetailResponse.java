package saviing.bank.account.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import saviing.bank.account.application.port.in.result.ProductDetailResult;

/**
 * 상품 상세 조회 응답 DTO입니다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "상품 상세 조회 응답")
public record ProductDetailResponse(
    @Schema(description = "상품 ID", example = "2")
    Long productId,
    @Schema(description = "상품명", example = "자유적금")
    String productName,
    @Schema(description = "상품 코드", example = "FREE_SAVINGS")
    String productCode,
    @Schema(description = "상품 분류", example = "INSTALLMENT_SAVINGS")
    String productCategory,
    @Schema(description = "상품 설명", example = "매월 자유롭게 납입 가능한 적금 상품")
    String description,
    @Schema(description = "복리 방식", example = "DAILY")
    String compoundingType,
    @Schema(description = "이자율 정보")
    InterestRateDto interestRate,
    @Schema(description = "적금 설정 정보")
    SavingsConfigDto savingsConfig,
    @Schema(description = "자유입출금 설정 정보")
    DemandDepositConfigDto demandDepositConfig
) {

    public static ProductDetailResponse from(ProductDetailResult result) {
        return new ProductDetailResponse(
            result.productId(),
            result.productName(),
            result.productCode(),
            result.productCategory(),
            result.description(),
            result.compoundingType(),
            mapInterest(result),
            mapSavings(result),
            mapDemandDeposit(result)
        );
    }

    private static InterestRateDto mapInterest(ProductDetailResult result) {
        if (result.minInterestRateBps() == null && result.maxInterestRateBps() == null) {
            return null;
        }
        return new InterestRateDto(result.minInterestRateBps(), result.maxInterestRateBps());
    }

    private static SavingsConfigDto mapSavings(ProductDetailResult result) {
        if (result.defaultPaymentCycle() == null
            && result.minPaymentAmount() == null
            && result.maxPaymentAmount() == null
            && result.termConstraints() == null) {
            return null;
        }

        TermConstraintsDto termConstraints = null;
        if (result.termConstraints() != null) {
            termConstraints = new TermConstraintsDto(
                result.termConstraints().minValue(),
                result.termConstraints().maxValue(),
                result.termConstraints().unit(),
                result.termConstraints().stepValue()
            );
        }

        return new SavingsConfigDto(
            result.defaultPaymentCycle(),
            result.minPaymentAmount(),
            result.maxPaymentAmount(),
            termConstraints
        );
    }

    private static DemandDepositConfigDto mapDemandDeposit(ProductDetailResult result) {
        if (result.minimumBalance() == null) {
            return null;
        }
        return new DemandDepositConfigDto(result.minimumBalance());
    }

    public record InterestRateDto(
        @Schema(description = "최소 금리 (베이시스 포인트)", example = "250")
        Short minBps,
        @Schema(description = "최대 금리 (베이시스 포인트)", example = "450")
        Short maxBps
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SavingsConfigDto(
        @Schema(description = "기본 납입 주기", example = "MONTHLY")
        String defaultPaymentCycle,
        @Schema(description = "최소 납입 금액", example = "10000")
        Long minPaymentAmount,
        @Schema(description = "최대 납입 금액", example = "1000000")
        Long maxPaymentAmount,
        @Schema(description = "기간 제약 정보")
        TermConstraintsDto termConstraints
    ) {}

    public record TermConstraintsDto(
        @Schema(description = "최소 기간 값", example = "1")
        Integer minValue,
        @Schema(description = "최대 기간 값", example = "15")
        Integer maxValue,
        @Schema(description = "기간 단위", example = "WEEKS")
        String unit,
        @Schema(description = "증가 단위 값", example = "1")
        Integer stepValue
    ) {}

    public record DemandDepositConfigDto(
        @Schema(description = "최소 잔액", example = "0")
        Long minimumBalance
    ) {}
}
