package saviing.bank.account.domain.vo;

import java.util.Objects;

public record AccountId(Long value) {
    
    public AccountId {
        Objects.requireNonNull(value, "계좌ID는 필수입니다");
        
        if (value <= 0) {
            throw new IllegalArgumentException("계좌ID는 양수여야 합니다");
        }
    }
    
    public static AccountId of(Long value) {
        return new AccountId(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}