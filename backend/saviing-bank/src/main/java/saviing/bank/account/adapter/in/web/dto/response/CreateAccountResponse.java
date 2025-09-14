package saviing.bank.account.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import saviing.bank.account.application.port.in.result.CreateAccountResult;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateAccountResponse(
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long accountId,

    String accountNumber,

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long customerId,

    ProductInfo product,
    String compoundingType,

    // 적금 정보 (적금 계좌가 아닌 경우 null)
    SavingsInfo savingsInfo,

    String status,

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    Instant openedAt,

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    Instant closedAt,

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    Instant lastAccrualAt,

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    Instant lastRateChangeAt,

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    Instant createdAt,

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    Instant updatedAt,

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long balance,

    BigDecimal interestAccrued,
    BigDecimal baseRatePercent,
    BigDecimal bonusRatePercent,
    BigDecimal totalRatePercent
) {

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ProductInfo(
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Long id,
        String name,
        String code,
        String category
    ) {}

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SavingsInfo(
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Long targetAmount,
        String termPeriod,
        LocalDate maturityDate,
        String maturityWithdrawalAccount,
        BigDecimal achievementRate
    ) {}
    
    public static CreateAccountResponse from(CreateAccountResult result) {
        // 적금 정보 변환 (적금 계좌인 경우만)
        SavingsInfo savingsInfo = null;
        if (result.savingsInfo() != null) {
            savingsInfo = SavingsInfo.builder()
                .targetAmount(result.savingsInfo().targetAmount())
                .termPeriod(result.savingsInfo().termPeriod())
                .maturityDate(result.savingsInfo().maturityDate())
                .maturityWithdrawalAccount(result.savingsInfo().maturityWithdrawalAccount())
                .achievementRate(result.savingsInfo().achievementRate())
                .build();
        }

        return CreateAccountResponse.builder()
            .accountId(result.accountId())
            .accountNumber(result.accountNumber())
            .customerId(result.customerId())
            .product(ProductInfo.builder()
                .id(result.product().id())
                .name(result.product().name())
                .code(result.product().code())
                .category(result.product().category())
                .build())
            .compoundingType(result.compoundingType())
            .savingsInfo(savingsInfo)
            .status(result.status())
            .openedAt(result.openedAt())
            .closedAt(result.closedAt())
            .lastAccrualAt(result.lastAccrualAt())
            .lastRateChangeAt(result.lastRateChangeAt())
            .createdAt(result.createdAt())
            .updatedAt(result.updatedAt())
            .balance(result.balance())
            .interestAccrued(result.interestAccrued())
            .baseRatePercent(result.baseRatePercent())
            .bonusRatePercent(result.bonusRatePercent())
            .totalRatePercent(result.totalRatePercent())
            .build();
    }
}