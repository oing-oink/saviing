package saviing.bank.account.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.AccountStatus;
import saviing.bank.account.domain.model.CompoundingType;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.AccountNumber;
import saviing.bank.account.domain.vo.BasisPoints;
import saviing.bank.account.domain.vo.MoneyWon;
import saviing.bank.account.domain.vo.ProductId;

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
    
    @Column(name = "payout_account_id")
    private Long payoutAccountId;
    
    @Column(name = "goal_amount")
    private Long goalAmount;
    
    @Column(name = "term_months")
    private Short termMonths;
    
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
    
    public static AccountJpaEntity fromDomain(Account account) {
        AccountJpaEntity entity = new AccountJpaEntity();
        
        if (account.getId() != null) {
            entity.id = account.getId().value();
        }
        entity.accountNumber = account.getAccountNumber().value();
        entity.customerId = account.getCustomerId();
        entity.productId = account.getProductId().value();
        entity.compoundingType = account.getCompoundingType();
        if (account.getPayoutAccountId() != null) {
            entity.payoutAccountId = account.getPayoutAccountId().value();
        }
        if (account.getGoalAmount() != null) {
            entity.goalAmount = account.getGoalAmount().amount();
        }
        entity.termMonths = account.getTermMonths();
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
            payoutAccountId != null ? AccountId.of(payoutAccountId) : null,
            goalAmount != null ? MoneyWon.of(goalAmount) : null,
            termMonths,
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