package saviing.bank.account.domain.vo;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import saviing.bank.common.vo.MoneyWon;
import static org.assertj.core.api.Assertions.*;

class MoneyWonTests {
    
    @Test
    void 유효한_금액으로_생성_성공() {
        assertThatNoException().isThrownBy(() -> {
            MoneyWon.of(0);
            MoneyWon.of(1000);
            MoneyWon.of(1000000);
        });
    }
    
    @Test
    void 음수_금액_입력시_예외_발생() {
        assertThatThrownBy(() -> MoneyWon.of(-1))
            .isInstanceOf(IllegalArgumentException.class);
        
        assertThatThrownBy(() -> MoneyWon.of(-1000))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void zero_팩토리_메서드() {
        MoneyWon zero = MoneyWon.zero();
        assertThat(zero.amount()).isEqualTo(0L);
        assertThat(zero.isZero()).isTrue();
    }
    
    @Test
    void 덧셈_연산() {
        MoneyWon money1 = MoneyWon.of(1000);
        MoneyWon money2 = MoneyWon.of(2000);
        MoneyWon result = money1.add(money2);
        
        assertThat(result.amount()).isEqualTo(3000L);
    }
    
    @Test
    void 뺄셈_연산() {
        MoneyWon money1 = MoneyWon.of(5000);
        MoneyWon money2 = MoneyWon.of(2000);
        MoneyWon result = money1.subtract(money2);
        
        assertThat(result.amount()).isEqualTo(3000L);
    }
    
    @Test
    void 뺄셈_결과가_음수일때_예외_발생() {
        MoneyWon money1 = MoneyWon.of(1000);
        MoneyWon money2 = MoneyWon.of(2000);
        
        assertThatThrownBy(() -> money1.subtract(money2))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void 곱셈_연산() {
        MoneyWon money = MoneyWon.of(1000);
        BigDecimal multiplier = BigDecimal.valueOf(1.5);
        MoneyWon result = money.multiply(multiplier);
        
        assertThat(result.amount()).isEqualTo(1500L);
    }
    
    @Test
    void 크기_비교() {
        MoneyWon money1 = MoneyWon.of(1000);
        MoneyWon money2 = MoneyWon.of(2000);
        MoneyWon money3 = MoneyWon.of(1000);
        
        assertThat(money2.isGreaterThan(money1)).isTrue();
        assertThat(money1.isGreaterThan(money2)).isFalse();
        assertThat(money1.isGreaterThanOrEqual(money3)).isTrue();
        assertThat(money1.isLessThan(money2)).isTrue();
    }
    
    @Test
    void 상태_확인() {
        MoneyWon zero = MoneyWon.zero();
        MoneyWon positive = MoneyWon.of(1000);
        
        assertThat(zero.isZero()).isTrue();
        assertThat(zero.isPositive()).isFalse();
        assertThat(positive.isZero()).isFalse();
        assertThat(positive.isPositive()).isTrue();
    }
    
    @Test
    void toString_테스트() {
        MoneyWon money = MoneyWon.of(1000);
        assertThat(money.toString()).isEqualTo("1000원");
    }
}