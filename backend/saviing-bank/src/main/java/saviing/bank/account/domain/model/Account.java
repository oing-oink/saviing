package saviing.bank.account.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import saviing.bank.account.domain.service.InterestAccrualService;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.AccountNumber;
import saviing.bank.account.domain.vo.BasisPoints;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.account.domain.vo.ProductId;
import saviing.bank.account.domain.vo.TermPeriod;
import saviing.bank.account.exception.InsufficientBalanceException;
import saviing.bank.account.exception.InvalidAccountStateException;
import saviing.bank.account.exception.InvalidAmountException;

/**
 * 계좌 도메인의 애그리거트 루트(Aggregate Root)입니다.
 *
 * 계좌와 관련된 모든 비즈니스 로직과 불변성을 보장하며,
 * 외부에서 계좌 도메인에 접근할 때의 진입점 역할을 합니다.
 *
 * 주요 기능:
 * - 계좌 개설, 입금, 출금, 동결/해제, 해지
 * - 금리 변경 및 이자 계산
 * - 계좌 상태 관리 및 비즈니스 룰 검증
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    
    private AccountId id;
    private AccountNumber accountNumber;
    private Long customerId;
    private ProductId productId;
    private CompoundingType compoundingType = CompoundingType.DAILY;
    // 적금 전용 필드들 (자유입출금은 null)
    private AccountNumber maturityWithdrawalAccount;  // 만기 시 출금계좌
    private MoneyWon targetAmount;                    // 목표금액
    private TermPeriod termPeriod;                    // 적금 기간 (26주 등)
    private LocalDate maturityDate;                   // 만기일
    private AccountStatus status = AccountStatus.ACTIVE;
    private Instant openedAt;
    private Instant closedAt;
    private Instant lastAccrualTs;
    private Instant lastRateChangeAt;
    private Instant createdAt;
    private Instant updatedAt;
    private MoneyWon balance = MoneyWon.zero();
    private BigDecimal interestAccrued = BigDecimal.ZERO;
    private BasisPoints baseRate = BasisPoints.zero();
    private BasisPoints bonusRate = BasisPoints.zero();
    
    private Account(
        @NonNull AccountNumber accountNumber,
        @NonNull Long customerId,
        @NonNull ProductId productId,
        @NonNull Instant now
    ) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.productId = productId;
        this.openedAt = now;
        this.createdAt = now;
        this.updatedAt = now;
        this.lastAccrualTs = now;
        this.lastRateChangeAt = now;
    }
    
    /**
     * 새로운 계좌를 개설합니다.
     *
     * @param accountNumber 계좌번호
     * @param customerId 고객 ID
     * @param productId 상품 ID
     * @param openedAt 개설 시점
     * @return 개설된 계좌
     */
    public static Account open(
        @NonNull AccountNumber accountNumber,
        @NonNull Long customerId,
        @NonNull ProductId productId,
        @NonNull Instant openedAt
    ) {
        return new Account(accountNumber, customerId, productId, openedAt);
    }

    /**
     * 기존 계좌 데이터로부터 계좌를 복원합니다.
     */
    public static Account restore(
        @NonNull AccountId id,
        @NonNull AccountNumber accountNumber,
        @NonNull Long customerId,
        @NonNull ProductId productId,
        @NonNull CompoundingType compoundingType,
        AccountNumber maturityWithdrawalAccount,
        MoneyWon targetAmount,
        TermPeriod termPeriod,
        LocalDate maturityDate,
        @NonNull AccountStatus status,
        @NonNull Instant openedAt,
        Instant closedAt,
        @NonNull Instant lastAccrualTs,
        @NonNull Instant lastRateChangeAt,
        @NonNull Instant createdAt,
        @NonNull Instant updatedAt,
        @NonNull MoneyWon balance,
        @NonNull BigDecimal interestAccrued,
        @NonNull BasisPoints baseRate,
        @NonNull BasisPoints bonusRate
    ) {
        Account account = new Account();
        account.id = id;
        account.accountNumber = accountNumber;
        account.customerId = customerId;
        account.productId = productId;
        account.compoundingType = compoundingType;
        account.maturityWithdrawalAccount = maturityWithdrawalAccount;
        account.targetAmount = targetAmount;
        account.termPeriod = termPeriod;
        account.maturityDate = maturityDate;
        account.status = status;
        account.openedAt = openedAt;
        account.closedAt = closedAt;
        account.lastAccrualTs = lastAccrualTs;
        account.lastRateChangeAt = lastRateChangeAt;
        account.createdAt = createdAt;
        account.updatedAt = updatedAt;
        account.balance = balance;
        account.interestAccrued = interestAccrued;
        account.baseRate = baseRate;
        account.bonusRate = bonusRate;
        return account;
    }
    
    /**
     * 기본 금리와 보너스 금리를 합친 총 금리를 반환합니다.
     *
     * @return 총 금리 (기본 금리 + 보너스 금리)
     */
    public BasisPoints getTotalRate() {
        return this.baseRate.add(this.bonusRate);
    }
    
    /**
     * 계좌에 입금합니다.
     *
     * @param amount 입금할 금액
     * @throws InvalidAmountException 0원 입금 시도 시
     * @throws InvalidAccountStateException 거래 불가능한 계좌 상태일 시
     */
    public void deposit(@NonNull MoneyWon amount) {
        if (amount.isZero()) {
            throw new InvalidAmountException(Map.of("amount", amount.amount()));
        }

        if (!status.canTransact()) {
            throw new InvalidAccountStateException(Map.of(
                "accountNumber", accountNumber,
                "currentStatus", status
            ));
        }

        this.balance = this.balance.add(amount);
        this.updatedAt = Instant.now();
    }
    
    /**
     * 계좌에서 출금합니다.
     *
     * @param amount 출금할 금액
     * @throws InvalidAmountException 0원 출금 시도 시
     * @throws InvalidAccountStateException 거래 불가능한 계좌 상태일 시
     * @throws InsufficientBalanceException 잔액이 부족할 시
     */
    public void withdraw(@NonNull MoneyWon amount) {
        if (amount.isZero()) {
            throw new InvalidAmountException(Map.of(
                "accountNumber", accountNumber,
                "amount", amount.amount(),
                "reason", "0원 출금 시도"
            ));
        }

        if (!status.canTransact()) {
            throw new InvalidAccountStateException(Map.of(
                "accountNumber", accountNumber,
                "currentStatus", status
            ));
        }

        if (this.balance.isLessThan(amount)) {
            throw new InsufficientBalanceException(Map.of(
                "accountNumber", this.accountNumber.value(),
                "currentBalance", this.balance.amount(),
                "requestAmount", amount.amount()
            ));
        }

        this.balance = this.balance.subtract(amount);
        this.updatedAt = Instant.now();
    }
    
    /**
     * 계좌를 동결 상태로 변경합니다.
     *
     * @throws InvalidAccountStateException 동결할 수 없는 계좌 상태일 시
     */
    public void freeze() {
        if (!status.canFreeze()) {
            throw new InvalidAccountStateException(Map.of(
                "accountNumber", accountNumber,
                "currentStatus", status,
                "newStatus", AccountStatus.FROZEN
            ));
        }

        this.status = AccountStatus.FROZEN;
        this.updatedAt = Instant.now();
    }
    
    /**
     * 계좌 동결을 해제하여 활성 상태로 변경합니다.
     *
     * @throws InvalidAccountStateException 동결 해제할 수 없는 계좌 상태일 시
     */
    public void unfreeze() {
        if (!status.canUnfreeze()) {
            throw new InvalidAccountStateException(Map.of(
                "accountNumber", accountNumber,
                "currentStatus", status,
                "newStatus", AccountStatus.ACTIVE
            ));
        }

        this.status = AccountStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }
    
    /**
     * 계좌를 해지합니다.
     *
     * @param closedAt 해지 시점
     * @throws InvalidAccountStateException 해지할 수 없는 계좌 상태일 시
     */
    public void close(@NonNull Instant closedAt) {
        if (!status.canClose()) {
            throw new InvalidAccountStateException(Map.of(
                "accountNumber", accountNumber,
                "currentStatus", status,
                "newStatus", AccountStatus.CLOSED
            ));
        }

        this.status = AccountStatus.CLOSED;
        this.closedAt = closedAt;
        this.updatedAt = Instant.now();
    }
    
    /**
     * 계좌의 기본 금리와 보너스 금리를 변경합니다.
     *
     * @param baseRate 새로운 기본 금리
     * @param bonusRate 새로운 보너스 금리
     * @param changedAt 금리 변경 시점
     */
    public void changeRates(
        @NonNull BasisPoints baseRate,
        @NonNull BasisPoints bonusRate,
        @NonNull Instant changedAt
    ) {
        this.baseRate = baseRate;
        this.bonusRate = bonusRate;
        this.lastRateChangeAt = changedAt;
        this.updatedAt = Instant.now();
    }

    /**
     * 계좌의 기본 금리만 변경합니다.
     *
     * @param baseRate 새로운 기본 금리
     * @param changedAt 금리 변경 시점
     */
    public void changeBaseRate(@NonNull BasisPoints baseRate, @NonNull Instant changedAt) {
        this.baseRate = baseRate;
        this.lastRateChangeAt = changedAt;
        this.updatedAt = Instant.now();
    }

    /**
     * 계좌의 보너스 금리만 변경합니다.
     *
     * @param bonusRate 새로운 보너스 금리
     * @param changedAt 금리 변경 시점
     */
    public void changeBonusRate(@NonNull BasisPoints bonusRate, @NonNull Instant changedAt) {
        this.bonusRate = bonusRate;
        this.lastRateChangeAt = changedAt;
        this.updatedAt = Instant.now();
    }

    /**
     * 현재 보너스 금리보다 높은 경우에만 보너스 금리를 업데이트합니다.
     * 게임 진행도에 따른 이자율 혜택 증가 정책을 구현하기 위해 사용됩니다.
     *
     * @param newBonusRate 새로 설정하려는 보너스 금리
     * @param changedAt 금리 변경 시점
     * @return 실제 설정된 현재 보너스 금리 (변경되지 않은 경우 기존값)
     */
    public BasisPoints updateBonusRateIfHigher(@NonNull BasisPoints newBonusRate, @NonNull Instant changedAt) {
        if (newBonusRate.isGreaterThan(this.bonusRate)) {
            this.bonusRate = newBonusRate;
            this.lastRateChangeAt = changedAt;
            this.updatedAt = Instant.now();
        }
        return this.bonusRate;
    }
    
    /**
     * 이자를 계산하여 누적 이자에 추가합니다.
     * 잔액이 0원인 경우 이자 계산을 생략합니다.
     *
     * @param asOf 이자 계산 기준 시점
     * @param accrualService 이자 계산 서비스
     */
    public void accrueInterest(@NonNull Instant asOf, @NonNull InterestAccrualService accrualService) {
        if (this.balance.isZero()) {
            this.lastAccrualTs = asOf;
            this.updatedAt = Instant.now();
            return;
        }

        BigDecimal additionalInterest = accrualService.computeAccrual(
            this.balance.amount(),
            this.interestAccrued,
            this.baseRate,
            this.bonusRate,
            this.compoundingType,
            this.lastAccrualTs,
            asOf
        );

        this.interestAccrued = this.interestAccrued.add(additionalInterest);
        this.lastAccrualTs = asOf;
        this.updatedAt = Instant.now();
    }
    
    /**
     * 누적된 이자를 계좌 잔액에 반영합니다.
     * 이월형 방식으로 정수 부분만 잔액에 추가하고, 소수점 이하는 다음 회차로 이월됩니다.
     *
     * @return 실제로 잔액에 반영된 이자 금액
     */
    public MoneyWon applyAccruedInterest() {
        if (this.interestAccrued.compareTo(BigDecimal.ZERO) <= 0) {
            return MoneyWon.zero();
        }

        // 이월형: floor(누적이자)만 지급, 소수점 이하는 이월
        long interestWon = this.interestAccrued.setScale(0, RoundingMode.FLOOR).longValue();
        MoneyWon appliedInterest = MoneyWon.of(interestWon);

        this.balance = this.balance.add(MoneyWon.of(interestWon));
        this.interestAccrued = this.interestAccrued.subtract(BigDecimal.valueOf(interestWon));
        this.updatedAt = Instant.now();

        return appliedInterest;
    }

    /**
     * 적금 설정을 추가합니다 (계좌 생성 시 사용).
     *
     * @param targetAmount 목표금액
     * @param termPeriod 적금 기간
     * @param maturityWithdrawalAccount 만기 시 출금계좌 (선택사항)
     * @param now 현재 시점
     */
    public void setSavingsSettings(
        @NonNull MoneyWon targetAmount,
        @NonNull TermPeriod termPeriod,
        AccountNumber maturityWithdrawalAccount,
        @NonNull Instant now
    ) {
        this.targetAmount = targetAmount;
        this.termPeriod = termPeriod;
        this.maturityWithdrawalAccount = maturityWithdrawalAccount;

        // 만기일 계산 (개설일 + termPeriod)
        this.maturityDate = this.openedAt.atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
            .plusDays(termPeriod.toDays());

        this.updatedAt = now;
    }

    /**
     * 적금 계좌의 목표 금액 및 만기 출금 계좌를 변경합니다.
     *
     * @param targetAmount 변경할 목표 금액 (null이면 변경하지 않음)
     * @param maturityWithdrawalAccount 변경할 만기 출금 계좌 (null이면 해제)
     * @param now 변경 시각
     * @throws InvalidAccountStateException 적금 계좌가 아니거나 해지된 계좌인 경우
     */
    public void updateSavingsSettings(
        MoneyWon targetAmount,
        AccountNumber maturityWithdrawalAccount,
        @NonNull Instant now
    ) {
        if (!isSavingsAccount()) {
            throw new InvalidAccountStateException(Map.of(
                "accountNumber", accountNumber,
                "reason", "NOT_SAVINGS_ACCOUNT"
            ));
        }

        if (status == AccountStatus.CLOSED) {
            throw new InvalidAccountStateException(Map.of(
                "accountNumber", accountNumber,
                "currentStatus", status,
                "action", "UPDATE_SAVINGS_SETTINGS"
            ));
        }

        if (targetAmount != null) {
            this.targetAmount = targetAmount;
        }

        this.maturityWithdrawalAccount = maturityWithdrawalAccount;
        this.updatedAt = now;
    }

    /**
     * 적금의 목표 달성률을 계산합니다.
     *
     * @return 목표 대비 현재 잔액 비율 (0.0 ~ 1.0 이상)
     */
    public BigDecimal getTargetAchievementRate() {
        if (targetAmount == null || targetAmount.isZero()) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(balance.amount())
            .divide(BigDecimal.valueOf(targetAmount.amount()), 4, RoundingMode.HALF_UP);
    }

    /**
     * 적금 계좌인지 확인합니다.
     *
     * @return 적금 계좌이면 true
     */
    public boolean isSavingsAccount() {
        return termPeriod != null;
    }
}
