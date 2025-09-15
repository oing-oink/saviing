package saviing.bank.account.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import saviing.bank.account.domain.vo.ProductId;
import saviing.bank.account.domain.service.InterestAccrualService;
import saviing.bank.account.domain.vo.AccountNumber;
import saviing.bank.account.domain.vo.BasisPoints;
import saviing.bank.account.domain.vo.MoneyWon;
import saviing.bank.account.exception.InsufficientBalanceException;
import saviing.bank.account.exception.InvalidAccountStateException;
import saviing.bank.account.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountTests {
    
    @Mock
    private InterestAccrualService interestAccrualService;
    
    private Account account;
    private final Instant now = Instant.now();
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = Account.open(
            new AccountNumber("1234567890"),
            1L,
            ProductId.of(1L),
            now
        );
    }
    
    @Test
    void 계좌_생성_성공() {
        // Given - setUp에서 계좌 생성됨
        
        // When - 계좌가 생성됨
        
        // Then - 초기 상태 검증
        assertThat(account.getId()).isNull();
        assertThat(account.getAccountNumber().value()).isEqualTo("1234567890");
        assertThat(account.getCustomerId()).isEqualTo(1L);
        assertThat(account.getProductId()).isEqualTo(ProductId.of(1L));
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.getBalance().amount()).isEqualTo(0L);
    }
    
    @Test
    void 입금_성공() {
        // Given
        MoneyWon depositAmount = MoneyWon.of(10000);
        
        // When
        account.deposit(depositAmount);
        
        // Then
        assertThat(account.getBalance()).isEqualTo(depositAmount);
    }
    
    @Test
    void 영원_입금시_예외_발생() {
        // Given
        MoneyWon zeroAmount = MoneyWon.zero();
        
        // When & Then
        assertThatThrownBy(() -> account.deposit(zeroAmount))
            .isInstanceOf(InvalidAmountException.class);
    }
    
    @Test
    void 동결된_계좌에_입금시_예외_발생() {
        // Given
        account.freeze();
        MoneyWon depositAmount = MoneyWon.of(1000);
        
        // When & Then
        assertThatThrownBy(() -> account.deposit(depositAmount))
            .isInstanceOf(InvalidAccountStateException.class);
    }
    
    @Test
    void 출금_성공() {
        // Given
        account.deposit(MoneyWon.of(10000));
        MoneyWon withdrawAmount = MoneyWon.of(5000);
        
        // When
        account.withdraw(withdrawAmount);
        
        // Then
        assertThat(account.getBalance().amount()).isEqualTo(5000L);
    }
    
    @Test
    void 잔액_부족시_출금_실패() {
        // Given
        account.deposit(MoneyWon.of(1000));
        MoneyWon withdrawAmount = MoneyWon.of(2000);
        
        // When & Then
        assertThatThrownBy(() -> account.withdraw(withdrawAmount))
            .isInstanceOf(InsufficientBalanceException.class);
    }
    
    @Test
    void 영원_출금시_예외_발생() {
        // Given
        MoneyWon zeroAmount = MoneyWon.zero();
        
        // When & Then
        assertThatThrownBy(() -> account.withdraw(zeroAmount))
            .isInstanceOf(InvalidAmountException.class);
    }
    
    @Test
    void 계좌_동결_성공() {
        // Given - 활성 계좌 상태
        
        // When
        account.freeze();
        
        // Then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.FROZEN);
    }
    
    @Test
    void 이미_동결된_계좌_동결시_예외_발생() {
        // Given
        account.freeze();
        
        // When & Then
        assertThatThrownBy(() -> account.freeze())
            .isInstanceOf(InvalidAccountStateException.class);
    }
    
    @Test
    void 동결_해제_성공() {
        // Given
        account.freeze();
        
        // When
        account.unfreeze();
        
        // Then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }
    
    @Test
    void 활성_계좌_동결해제시_예외_발생() {
        // Given - 활성 계좌 상태
        
        // When & Then
        assertThatThrownBy(() -> account.unfreeze())
            .isInstanceOf(InvalidAccountStateException.class);
    }
    
    @Test
    void 계좌_해지_성공() {
        // Given
        Instant closedAt = now.plusSeconds(3600);
        
        // When
        account.close(closedAt);
        
        // Then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(account.getClosedAt()).isEqualTo(closedAt);
    }
    
    @Test
    void 금리_변경_성공() {
        // Given
        BasisPoints baseRate = BasisPoints.of(250);
        BasisPoints bonusRate = BasisPoints.of(50);
        Instant changedAt = now.plusSeconds(1800);
        
        // When
        account.changeRates(baseRate, bonusRate, changedAt);
        
        // Then
        assertThat(account.getBaseRate()).isEqualTo(baseRate);
        assertThat(account.getBonusRate()).isEqualTo(bonusRate);
        assertThat(account.getTotalRate().value()).isEqualTo((short) 300);
        assertThat(account.getLastRateChangeAt()).isEqualTo(changedAt);
    }
    
    @Test
    void 이자_계산_성공() {
        // Given
        account.deposit(MoneyWon.of(100000));
        when(interestAccrualService.computeAccrual(anyLong(), any(), any(), any(), any(), any(), any()))
            .thenReturn(BigDecimal.valueOf(100.5));
        Instant accrualTime = now.plusSeconds(86400);
        
        // When
        account.accrueInterest(accrualTime, interestAccrualService);
        
        // Then
        assertThat(account.getInterestAccrued()).isEqualTo(BigDecimal.valueOf(100.5));
        assertThat(account.getLastAccrualTs()).isEqualTo(accrualTime);
    }
    
    @Test
    void 잔액_없을때_이자_계산_스킵() {
        // Given
        when(interestAccrualService.computeAccrual(anyLong(), any(), any(), any(), any(), any(), any()))
            .thenReturn(BigDecimal.valueOf(100.0));
        Instant accrualTime = now.plusSeconds(86400);
        
        // When
        account.accrueInterest(accrualTime, interestAccrualService);
        
        // Then
        verify(interestAccrualService, never()).computeAccrual(anyLong(), any(), any(), any(), any(), any(), any());
        assertThat(account.getLastAccrualTs()).isEqualTo(accrualTime);
    }
    
    @Test
    void 이월형_이자_적용_성공() {
        // Given
        account.deposit(MoneyWon.of(100000));
        when(interestAccrualService.computeAccrual(anyLong(), any(), any(), any(), any(), any(), any()))
            .thenReturn(BigDecimal.valueOf(150.7));
        account.accrueInterest(now.plusSeconds(86400), interestAccrualService);
        
        // When
        MoneyWon appliedInterest = account.applyAccruedInterest();
        
        // Then - 이월형: floor(150.7) = 150원만 지급, 0.7은 이월
        assertThat(appliedInterest.amount()).isEqualTo(150L);
        assertThat(account.getBalance().amount()).isEqualTo(100150L);
        assertThat(account.getInterestAccrued()).isEqualTo(BigDecimal.valueOf(0.7));
    }
    
    @Test
    void 미지급_이자_1원_미만_이월() {
        // Given
        account.deposit(MoneyWon.of(100000));
        when(interestAccrualService.computeAccrual(anyLong(), any(), any(), any(), any(), any(), any()))
            .thenReturn(BigDecimal.valueOf(0.8));
        account.accrueInterest(now.plusSeconds(86400), interestAccrualService);
        
        // When
        MoneyWon appliedInterest = account.applyAccruedInterest();
        
        // Then - 이월형: floor(0.8) = 0원 지급, 0.8 전액 이월
        assertThat(appliedInterest.amount()).isEqualTo(0L);
        assertThat(account.getBalance().amount()).isEqualTo(100000L);
        assertThat(account.getInterestAccrued()).isEqualTo(BigDecimal.valueOf(0.8));
    }
    
    @Test
    void 이월된_이자가_누적되어_1원_이상시_지급() {
        // Given
        account.deposit(MoneyWon.of(100000));
        
        // 첫 번째 이자 계산: 0.6원 이월
        when(interestAccrualService.computeAccrual(anyLong(), any(), any(), any(), any(), any(), any()))
            .thenReturn(BigDecimal.valueOf(0.6));
        account.accrueInterest(now.plusSeconds(86400), interestAccrualService);
        account.applyAccruedInterest();
        
        // 두 번째 이자 계산: 기존 0.6 + 새로운 0.5 = 1.1원
        when(interestAccrualService.computeAccrual(anyLong(), any(), any(), any(), any(), any(), any()))
            .thenReturn(BigDecimal.valueOf(0.5));
        account.accrueInterest(now.plusSeconds(172800), interestAccrualService);
        
        // When
        MoneyWon appliedInterest = account.applyAccruedInterest();
        
        // Then - 이월형: floor(1.1) = 1원 지급, 0.1원 이월
        assertThat(appliedInterest.amount()).isEqualTo(1L);
        assertThat(account.getBalance().amount()).isEqualTo(100001L);
        assertThat(account.getInterestAccrued()).isEqualTo(BigDecimal.valueOf(0.1));
    }
    
    @Test
    void 여러번_이월_후_누적_지급() {
        // Given
        account.deposit(MoneyWon.of(100000));
        
        // 첫 번째: 0.3원 이월
        when(interestAccrualService.computeAccrual(anyLong(), any(), any(), any(), any(), any(), any()))
            .thenReturn(BigDecimal.valueOf(0.3));
        account.accrueInterest(now.plusSeconds(86400), interestAccrualService);
        account.applyAccruedInterest();
        assertThat(account.getInterestAccrued()).isEqualTo(BigDecimal.valueOf(0.3));
        
        // 두 번째: 0.3 + 0.4 = 0.7원 이월
        when(interestAccrualService.computeAccrual(anyLong(), any(), any(), any(), any(), any(), any()))
            .thenReturn(BigDecimal.valueOf(0.4));
        account.accrueInterest(now.plusSeconds(172800), interestAccrualService);
        account.applyAccruedInterest();
        assertThat(account.getInterestAccrued()).isEqualTo(BigDecimal.valueOf(0.7));
        
        // 세 번째: 0.7 + 0.5 = 1.2원 -> 1원 지급, 0.2원 이월
        when(interestAccrualService.computeAccrual(anyLong(), any(), any(), any(), any(), any(), any()))
            .thenReturn(BigDecimal.valueOf(0.5));
        account.accrueInterest(now.plusSeconds(259200), interestAccrualService);
        
        // When
        MoneyWon appliedInterest = account.applyAccruedInterest();
        
        // Then
        assertThat(appliedInterest.amount()).isEqualTo(1L);
        assertThat(account.getBalance().amount()).isEqualTo(100001L);
        assertThat(account.getInterestAccrued()).isEqualTo(BigDecimal.valueOf(0.2));
    }
}