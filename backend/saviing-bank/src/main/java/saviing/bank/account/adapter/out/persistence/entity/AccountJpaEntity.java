package saviing.bank.account.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import saviing.common.annotation.ExecutionTime;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.AccountStatus;
import saviing.bank.account.domain.model.CompoundingType;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.AccountNumber;
import saviing.bank.account.domain.vo.BasisPoints;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.account.domain.vo.ProductId;
import saviing.bank.account.domain.vo.TermPeriod;
import saviing.bank.account.domain.model.TermUnit;

@ExecutionTime
@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;
    
    @Column(name = "account_number", length = 32, nullable = false, unique = true)
    private String accountNumber;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "compounding_type", nullable = false)
    private CompoundingType compoundingType = CompoundingType.DAILY;
    
    // 적금 전용 필드들
    @Column(name = "maturity_withdrawal_account", length = 32)
    private String maturityWithdrawalAccount;

    @Column(name = "target_amount")
    private Long targetAmount;

    @Column(name = "term_period_value")
    private Integer termPeriodValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "term_period_unit")
    private TermUnit termPeriodUnit;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;
    
    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;
    
    @Column(name = "closed_at")
    private Instant closedAt;
    
    @Column(name = "last_accrual_ts")
    private Instant lastAccrualTs;
    
    @Column(name = "last_rate_change_at")
    private Instant lastRateChangeAt;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @Column(name = "balance", nullable = false)
    private Long balance = 0L;
    
    @Column(name = "interest_accrued", precision = 20, scale = 6, nullable = false)
    private BigDecimal interestAccrued = BigDecimal.ZERO;
    
    @Column(name = "base_rate_bps", nullable = false)
    private Short baseRateBps = 0;
    
    @Column(name = "bonus_rate_bps", nullable = false)
    private Short bonusRateBps = 0;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;
    
    public static AccountJpaEntity fromDomain(Account account) {
        AccountJpaEntity entity = new AccountJpaEntity();
        
        if (account.getId() != null) {
            entity.id = account.getId().value();
        }
        entity.accountNumber = account.getAccountNumber().value();
        entity.customerId = account.getCustomerId();
        entity.productId = account.getProductId().value();
        entity.compoundingType = account.getCompoundingType();

        // 적금 필드 매핑
        if (account.getMaturityWithdrawalAccount() != null) {
            entity.maturityWithdrawalAccount = account.getMaturityWithdrawalAccount().value();
        }
        if (account.getTargetAmount() != null) {
            entity.targetAmount = account.getTargetAmount().amount();
        }
        if (account.getTermPeriod() != null) {
            entity.termPeriodValue = account.getTermPeriod().value();
            entity.termPeriodUnit = account.getTermPeriod().unit();
        }
        entity.maturityDate = account.getMaturityDate();
        entity.status = account.getStatus();
        entity.openedAt = account.getOpenedAt();
        entity.closedAt = account.getClosedAt();
        entity.lastAccrualTs = account.getLastAccrualTs();
        entity.lastRateChangeAt = account.getLastRateChangeAt();
        entity.createdAt = account.getCreatedAt();
        entity.updatedAt = account.getUpdatedAt();
        entity.balance = account.getBalance().amount();
        entity.interestAccrued = account.getInterestAccrued();
        entity.baseRateBps = account.getBaseRate().value();
        entity.bonusRateBps = account.getBonusRate().value();

        return entity;
    }
    
    public Account toDomain() {
        return Account.restore(
            id != null ? AccountId.of(id) : null,
            new AccountNumber(accountNumber),
            customerId,
            ProductId.of(productId),
            compoundingType,
            maturityWithdrawalAccount != null ? new AccountNumber(maturityWithdrawalAccount) : null,
            targetAmount != null ? MoneyWon.of(targetAmount) : null,
            (termPeriodValue != null && termPeriodUnit != null) ?
                TermPeriod.of(termPeriodValue, termPeriodUnit) : null,
            maturityDate,
            status,
            openedAt,
            closedAt,
            lastAccrualTs,
            lastRateChangeAt,
            createdAt,
            updatedAt,
            MoneyWon.of(balance),
            interestAccrued,
            BasisPoints.of(baseRateBps),
            BasisPoints.of(bonusRateBps)
        );
    }
}