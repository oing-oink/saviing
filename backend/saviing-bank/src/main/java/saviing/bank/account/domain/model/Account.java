package saviing.bank.account.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import saviing.bank.account.domain.exception.InsufficientBalanceException;
import saviing.bank.account.domain.exception.InvalidAccountStateException;
import saviing.bank.account.domain.exception.InvalidAmountException;
import saviing.bank.account.domain.service.InterestAccrualService;
import saviing.bank.account.domain.vo.AccountNumber;
import saviing.bank.account.domain.vo.BasisPoints;
import saviing.bank.account.domain.vo.MoneyWon;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;
    
    @Column(name = "account_number", length = 32, nullable = false, unique = true)
    private String accountNumber;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType productType;
    
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
    
    private Account(
        AccountNumber accountNumber,
        Long customerId, 
        ProductType productType,
        Instant now
    ) {
        this.accountNumber = Objects.requireNonNull(accountNumber, "계좌번호는 필수입니다").value();
        this.customerId = Objects.requireNonNull(customerId, "고객ID는 필수입니다");
        this.productType = Objects.requireNonNull(productType, "상품유형은 필수입니다");
        this.openedAt = Objects.requireNonNull(now, "개설시간은 필수입니다");
        this.createdAt = now;
        this.updatedAt = now;
        this.lastAccrualTs = now;
        this.lastRateChangeAt = now;
    }
    
    public static Account open(
        AccountNumber accountNumber,
        Long customerId,
        ProductType productType,
        Instant openedAt
    ) {
        return new Account(accountNumber, customerId, productType, openedAt);
    }
    
    public MoneyWon getBalance() {
        return MoneyWon.of(this.balance);
    }
    
    public BasisPoints getBaseRate() {
        return BasisPoints.of(this.baseRateBps);
    }
    
    public BasisPoints getBonusRate() {
        return BasisPoints.of(this.bonusRateBps);
    }
    
    public BasisPoints getTotalRate() {
        return getBaseRate().add(getBonusRate());
    }
    
    public AccountNumber getAccountNumber() {
        return new AccountNumber(this.accountNumber);
    }
    
    public void deposit(MoneyWon amount) {
        Objects.requireNonNull(amount, "입금액은 필수입니다");
        
        if (amount.isZero()) {
            throw InvalidAmountException.zeroAmount();
        }
        
        if (!status.canTransact()) {
            throw new InvalidAccountStateException(status, "입금");
        }
        
        this.balance += amount.amount();
        this.updatedAt = Instant.now();
    }
    
    public void withdraw(MoneyWon amount) {
        Objects.requireNonNull(amount, "출금액은 필수입니다");
        
        if (amount.isZero()) {
            throw InvalidAmountException.zeroAmount();
        }
        
        if (!status.canTransact()) {
            throw new InvalidAccountStateException(status, "출금");
        }
        
        MoneyWon currentBalance = MoneyWon.of(this.balance);
        if (currentBalance.isLessThan(amount)) {
            throw new InsufficientBalanceException(this.accountNumber, amount.amount(), this.balance);
        }
        
        this.balance -= amount.amount();
        this.updatedAt = Instant.now();
    }
    
    public void freeze() {
        if (!status.canFreeze()) {
            throw new InvalidAccountStateException(status, "동결");
        }
        
        this.status = AccountStatus.FROZEN;
        this.updatedAt = Instant.now();
    }
    
    public void unfreeze() {
        if (!status.canUnfreeze()) {
            throw new InvalidAccountStateException(status, "동결 해제");
        }
        
        this.status = AccountStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }
    
    public void close(Instant closedAt) {
        Objects.requireNonNull(closedAt, "해지시간은 필수입니다");
        
        if (!status.canClose()) {
            throw new InvalidAccountStateException(status, "해지");
        }
        
        this.status = AccountStatus.CLOSED;
        this.closedAt = closedAt;
        this.updatedAt = Instant.now();
    }
    
    public void changeRates(BasisPoints baseRate, BasisPoints bonusRate, Instant changedAt) {
        Objects.requireNonNull(baseRate, "기본금리는 필수입니다");
        Objects.requireNonNull(bonusRate, "보너스금리는 필수입니다");
        Objects.requireNonNull(changedAt, "변경시간은 필수입니다");
        
        this.baseRateBps = baseRate.value();
        this.bonusRateBps = bonusRate.value();
        this.lastRateChangeAt = changedAt;
        this.updatedAt = Instant.now();
    }
    
    public void accrueInterest(Instant asOf, InterestAccrualService accrualService) {
        Objects.requireNonNull(asOf, "기준시간은 필수입니다");
        Objects.requireNonNull(accrualService, "이자계산서비스는 필수입니다");
        
        if (this.balance <= 0) {
            this.lastAccrualTs = asOf;
            this.updatedAt = Instant.now();
            return;
        }
        
        BigDecimal additionalInterest = accrualService.computeAccrual(
            this.balance,
            this.interestAccrued,
            getBaseRate(),
            getBonusRate(),
            this.compoundingType,
            this.lastAccrualTs,
            asOf
        );
        
        this.interestAccrued = this.interestAccrued.add(additionalInterest);
        this.lastAccrualTs = asOf;
        this.updatedAt = Instant.now();
    }
    
    public MoneyWon applyAccruedInterest() {
        if (this.interestAccrued.compareTo(BigDecimal.ZERO) <= 0) {
            return MoneyWon.zero();
        }
        
        // 이월형: floor(누적이자)만 지급, 소수점 이하는 이월
        long interestWon = this.interestAccrued.setScale(0, RoundingMode.FLOOR).longValue();
        MoneyWon appliedInterest = MoneyWon.of(interestWon);
        
        this.balance += interestWon;
        this.interestAccrued = this.interestAccrued.subtract(BigDecimal.valueOf(interestWon));
        this.updatedAt = Instant.now();
        
        return appliedInterest;
    }
}