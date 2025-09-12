package saviing.bank.account.domain.vo;

import java.util.Objects;

public record AccountNumber(String value) {
    
    private static final String VALID_PATTERN = "^[0-9]{1,32}$";
    
    public AccountNumber {
        Objects.requireNonNull(value, "계좌번호는 필수입니다");
        
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("계좌번호는 공백일 수 없습니다");
        }
        
        if (value.length() > 32) {
            throw new IllegalArgumentException("계좌번호는 32자를 초과할 수 없습니다");
        }
        
        if (!value.matches(VALID_PATTERN)) {
            throw new IllegalArgumentException("계좌번호는 숫자만 허용됩니다");
        }
    }
    
    @Override
    public String toString() {
        return value;
    }
}