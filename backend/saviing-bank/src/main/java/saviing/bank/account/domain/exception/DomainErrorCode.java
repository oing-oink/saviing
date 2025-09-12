package saviing.bank.account.domain.exception;

public enum DomainErrorCode {
    
    // 잔액 관련 에러
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "잔액이 부족합니다"),
    
    // 계좌 상태 관련 에러
    INVALID_ACCOUNT_STATE("INVALID_ACCOUNT_STATE", "계좌 상태가 유효하지 않습니다"),
    
    // 금액 관련 에러
    INVALID_AMOUNT("INVALID_AMOUNT", "유효하지 않은 금액입니다"),
    
    // 금리 관련 에러
    INVALID_RATE("INVALID_RATE", "유효하지 않은 금리입니다");
    
    private final String code;
    private final String message;
    
    DomainErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}