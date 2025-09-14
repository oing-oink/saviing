package saviing.bank.account.application.port.in.result;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.Product;

@Builder
public record CreateAccountResult(
    Long accountId,
    String accountNumber,
    Long customerId,
    ProductInfo product,
    String compoundingType,
    // 적금 정보 (적금 계좌가 아닌 경우 null)
    SavingsInfo savingsInfo,
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

    @Builder
    public record ProductInfo(
        Long id,
        String name,
        String code,
        String category
    ) {}

    @Builder
    public record SavingsInfo(
        Long targetAmount,
        String termPeriod,
        LocalDate maturityDate,
        String maturityWithdrawalAccount,
        BigDecimal achievementRate
    ) {}
    
    public static CreateAccountResult from(Account account, Product product) {
        // 적금 정보 구성 (적금 계좌인 경우만)
        SavingsInfo savingsInfo = null;
        if (account.isSavingsAccount()) {
            savingsInfo = SavingsInfo.builder()
                .targetAmount(account.getTargetAmount().amount())
                .termPeriod(account.getTermPeriod().toString())
                .maturityDate(account.getMaturityDate())
                .maturityWithdrawalAccount(
                    account.getMaturityWithdrawalAccount() != null ?
                    account.getMaturityWithdrawalAccount().value() : null
                )
                .achievementRate(account.getTargetAchievementRate())
                .build();
        }

        return CreateAccountResult.builder()
            .accountId(account.getId() != null ? account.getId().value() : null)
            .accountNumber(account.getAccountNumber().value())
            .customerId(account.getCustomerId())
            .product(ProductInfo.builder()
                .id(product.getId().value())
                .name(product.getName())
                .code(product.getCode())
                .category(product.getCategory().getDescription())
                .build())
            .compoundingType(account.getCompoundingType().name())
            .savingsInfo(savingsInfo)
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