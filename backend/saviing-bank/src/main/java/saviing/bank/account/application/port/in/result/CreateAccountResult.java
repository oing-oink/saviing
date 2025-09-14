package saviing.bank.account.application.port.in.result;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import saviing.bank.account.domain.model.Account;

@Builder
public record CreateAccountResult(
    Long accountId,
    String accountNumber,
    Long customerId,
    String productType,
    String compoundingType,
    Long payoutAccountId,
    Long goalAmount,
    Short termMonths,
    LocalDate maturityDate,
    String status,
    Instant openedAt,
    Instant closedAt,
    Instant lastAccrualAt,
    Instant lastRateChangeAt,
    Instant createdAt,
    Instant updatedAt,
    Long balance,
    BigDecimal interestAccrued,
    BigDecimal baseRatePercent,
    BigDecimal bonusRatePercent,
    BigDecimal totalRatePercent
) {
    public static CreateAccountResult from(Account account) {
        return CreateAccountResult.builder()
            .accountId(account.getId() != null ? account.getId().value() : null)
            .accountNumber(account.getAccountNumber().value())
            .customerId(account.getCustomerId())
            .productType(account.getProductType().name())
            .compoundingType(account.getCompoundingType().name())
            .payoutAccountId(account.getPayoutAccountId() != null ? account.getPayoutAccountId().value() : null)
            .goalAmount(account.getGoalAmount() != null ? account.getGoalAmount().amount() : null)
            .termMonths(account.getTermMonths())
            .maturityDate(account.getMaturityDate())
            .status(account.getStatus().name())
            .openedAt(account.getOpenedAt())
            .closedAt(account.getClosedAt())
            .lastAccrualAt(account.getLastAccrualTs())
            .lastRateChangeAt(account.getLastRateChangeAt())
            .createdAt(account.getCreatedAt())
            .updatedAt(account.getUpdatedAt())
            .balance(account.getBalance().amount())
            .interestAccrued(account.getInterestAccrued())
            .baseRatePercent(account.getBaseRate().toPercent())
            .bonusRatePercent(account.getBonusRate().toPercent())
            .totalRatePercent(account.getTotalRate().toPercent())
            .build();
    }
}