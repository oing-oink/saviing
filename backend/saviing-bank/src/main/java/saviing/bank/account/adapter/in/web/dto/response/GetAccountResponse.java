package saviing.bank.account.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.NonNull;
import saviing.bank.account.application.port.in.result.GetAccountResult;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "계좌 조회 응답")
public record GetAccountResponse(
    @Schema(description = "계좌 ID", example = "1")
    Long accountId,

    @Schema(description = "계좌번호", example = "11012345678901234")
    String accountNumber,

    @Schema(description = "고객 ID", example = "1001")
    Long customerId,

    @Schema(description = "상품 정보")
    ProductDto product,

    @Schema(
        description = "복리 계산 방식",
        example = "SIMPLE",
        allowableValues = {"SIMPLE", "DAILY", "MONTH", "YEAR"}
    )
    String compoundingType,

    @Schema(
        description = "계좌 상태",
        example = "ACTIVE",
        allowableValues = {"ACTIVE", "FROZEN", "CLOSED"}
    )
    String status,

    @Schema(description = "개설일시", example = "2024-01-15T09:30:00Z")
    Instant openedAt,

    @Schema(description = "해지일시", example = "2024-12-31T17:00:00Z")
    Instant closedAt,

    @Schema(description = "마지막 이자 적용 일시", example = "2024-01-15T23:59:59Z")
    Instant lastAccrualTs,

    @Schema(description = "마지막 금리 변경 일시", example = "2024-01-15T09:30:00Z")
    Instant lastRateChangeAt,

    @Schema(description = "생성일시", example = "2024-01-15T09:30:00Z")
    Instant createdAt,

    @Schema(description = "수정일시", example = "2024-01-15T09:30:00Z")
    Instant updatedAt,

    @Schema(description = "잔액 (원)", example = "500000")
    Long balance,

    @Schema(description = "누적 이자 (원)", example = "1250.50")
    BigDecimal interestAccrued,

    @Schema(description = "기본 금리 (베이시스 포인트)", example = "250")
    Short baseRate,

    @Schema(description = "보너스 금리 (베이시스 포인트)", example = "50")
    Short bonusRate,

    @Schema(description = "적금 정보 (적금 계좌인 경우에만)")
    SavingsDto savings
) {

    public static GetAccountResponse from(@NonNull GetAccountResult result) {
        return GetAccountResponse.builder()
            .accountId(result.accountId())
            .accountNumber(result.accountNumber())
            .customerId(result.customerId())
            .compoundingType(result.compoundingType())
            .status(result.status())
            .openedAt(result.openedAt())
            .closedAt(result.closedAt())
            .lastAccrualTs(result.lastAccrualTs())
            .lastRateChangeAt(result.lastRateChangeAt())
            .createdAt(result.createdAt())
            .updatedAt(result.updatedAt())
            .balance(result.balance())
            .interestAccrued(result.interestAccrued())
            .baseRate(result.baseRate())
            .bonusRate(result.bonusRate())
            .product(ProductDto.from(result.product()))
            .savings(SavingsDto.from(result.savings()))
            .build();
    }
}