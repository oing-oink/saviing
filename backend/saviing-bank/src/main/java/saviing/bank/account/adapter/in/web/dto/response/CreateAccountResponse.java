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
    
    String productType,
    String compoundingType,
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long payoutAccountId,
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long goalAmount,
    
    Short termMonths,
    LocalDate maturityDate,
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
    public static CreateAccountResponse from(CreateAccountResult result) {
        return CreateAccountResponse.builder()
            .accountId(result.accountId())
            .accountNumber(result.accountNumber())
            .customerId(result.customerId())
            .productType(result.productType())
            .compoundingType(result.compoundingType())
            .payoutAccountId(result.payoutAccountId())
            .goalAmount(result.goalAmount())
            .termMonths(result.termMonths())
            .maturityDate(result.maturityDate())
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