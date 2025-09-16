package saviing.bank.transaction.exception;

public enum TransactionErrorType {
    TRANSACTION_NOT_FOUND("거래를 찾을 수 없습니다"),
    INVALID_TRANSACTION_AMOUNT("유효하지 않은 거래 금액입니다"),
    DUPLICATE_TRANSACTION("중복된 거래입니다"),
    INVALID_TRANSACTION_STATE("유효하지 않은 거래 상태입니다"),
    INSUFFICIENT_BALANCE("잔액이 부족합니다"),
    INVALID_ACCOUNT_STATE("유효하지 않은 계좌 상태입니다"),
    INVALID_VALUE_DATE("유효하지 않은 가치일입니다"),
    TRANSACTION_ALREADY_VOID("이미 무효화된 거래입니다");

    private final String message;

    TransactionErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}