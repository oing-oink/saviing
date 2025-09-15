package saviing.bank.account.application.port.in.result;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;

import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.Product;

@Builder
public record GetAccountResult(
    Long accountId,
    String accountNumber,
    Long customerId,
    String compoundingType,
    String status,
    Instant openedAt,
    Instant closedAt,
    Instant lastAccrualTs,
    Instant lastRateChangeAt,
    Instant createdAt,
    Instant updatedAt,
    Long balance,
    BigDecimal interestAccrued,
    Short baseRate,
    Short bonusRate,
    ProductInfo product,
    SavingsInfo savings
) {

    public static GetAccountResult from(@NonNull Account account, @NonNull Product product) {
        return GetAccountResult.builder()
            .accountId(account.getId().value())
            .accountNumber(account.getAccountNumber().value())
            .customerId(account.getCustomerId())
            .compoundingType(account.getCompoundingType().name())
            .status(account.getStatus().name())
            .openedAt(account.getOpenedAt())
            .closedAt(account.getClosedAt())
            .lastAccrualTs(account.getLastAccrualTs())
            .lastRateChangeAt(account.getLastRateChangeAt())
            .createdAt(account.getCreatedAt())
            .updatedAt(account.getUpdatedAt())
            .balance(account.getBalance().amount())
            .interestAccrued(account.getInterestAccrued())
            .baseRate(account.getBaseRate().value())
            .bonusRate(account.getBonusRate().value())
            .product(ProductInfo.from(product))
            .savings(SavingsInfo.from(account))
            .build();
    }
}