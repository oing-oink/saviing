package saviing.bank.account.domain.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AccountNumberTests {
    
    @Test
    void 유효한_계좌번호로_생성_성공() {
        assertThatNoException().isThrownBy(() -> {
            new AccountNumber("1234567890");
            new AccountNumber("123");
            new AccountNumber("9876543210987654321");
        });
    }
    
    @Test
    void null_입력시_예외_발생() {
        assertThatThrownBy(() -> new AccountNumber(null))
            .isInstanceOf(NullPointerException.class);
    }
    
    @Test
    void 빈_문자열_입력시_예외_발생() {
        assertThatThrownBy(() -> new AccountNumber(""))
            .isInstanceOf(IllegalArgumentException.class);
        
        assertThatThrownBy(() -> new AccountNumber("   "))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void 길이_초과시_예외_발생() {
        String longAccountNumber = "1".repeat(33);
        assertThatThrownBy(() -> new AccountNumber(longAccountNumber))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void 숫자가_아닌_문자_포함시_예외_발생() {
        assertThatThrownBy(() -> new AccountNumber("123ABC"))
            .isInstanceOf(IllegalArgumentException.class);
        
        assertThatThrownBy(() -> new AccountNumber("123-456"))
            .isInstanceOf(IllegalArgumentException.class);
        
        assertThatThrownBy(() -> new AccountNumber("123 456"))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void 동등성_비교() {
        AccountNumber account1 = new AccountNumber("123456");
        AccountNumber account2 = new AccountNumber("123456");
        AccountNumber account3 = new AccountNumber("654321");
        
        assertThat(account1).isEqualTo(account2);
        assertThat(account1).isNotEqualTo(account3);
        assertThat(account1.hashCode()).isEqualTo(account2.hashCode());
    }
    
    @Test
    void toString_테스트() {
        AccountNumber account = new AccountNumber("1234567890");
        assertThat(account.toString()).isEqualTo("1234567890");
    }
}