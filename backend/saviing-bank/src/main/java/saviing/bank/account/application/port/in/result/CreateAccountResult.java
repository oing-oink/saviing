package saviing.bank.account.application.port.in.result;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.AutoTransferSchedule;
import saviing.bank.account.domain.model.Product;

@Builder
public record CreateAccountResult(
    Long accountId,
    String accountNumber,
    Long customerId,
    String compoundingType,
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
    BigDecimal totalRatePercent,
    ProductInfo productInfo,
    SavingsInfo savingsInfo
) {
    
    public static CreateAccountResult from(Account account, Product product) {
        return from(account, product, null);
    }

    public static CreateAccountResult from(Account account, Product product, AutoTransferSchedule schedule) {
        return CreateAccountResult.builder()
            .accountId(account.getId() != null ? account.getId().value() : null)
            .accountNumber(account.getAccountNumber().value())
            .customerId(account.getCustomerId())
            .compoundingType(account.getCompoundingType().name())
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
            .productInfo(ProductInfo.from(product))
            .savingsInfo(SavingsInfo.from(account, schedule))
            .build();
    }
}
