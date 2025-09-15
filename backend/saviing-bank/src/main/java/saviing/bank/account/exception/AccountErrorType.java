package saviing.bank.account.exception;

public enum AccountErrorType {
    
    // 잔액 관련 에러
    INSUFFICIENT_BALANCE("잔액이 부족합니다"),
    
    // 계좌 관련 에러
    ACCOUNT_NOT_FOUND("계좌를 찾을 수 없습니다"),
    INVALID_ACCOUNT_STATE("계좌 상태가 유효하지 않습니다"),
    
    // 금액 관련 에러
    INVALID_AMOUNT("유효하지 않은 금액입니다"),
    
    // 금리 관련 에러
    INVALID_RATE("유효하지 않은 금리입니다"),

    // 상품 관련 에러
    INVALID_PRODUCT_TYPE("요청한 계좌 타입과 상품 타입이 일치하지 않습니다"),
    INVALID_SAVINGS_TERM("유효하지 않은 적금 기간입니다"),
    INVALID_TARGET_AMOUNT("유효하지 않은 목표금액입니다");
    
    private final String message;
    
    AccountErrorType(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}