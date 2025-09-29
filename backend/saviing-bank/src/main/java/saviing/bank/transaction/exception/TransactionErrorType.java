package saviing.bank.transaction.exception;

/**
 * 거래 예외 유형을 나타내는 열거형
 * 거래 도메인에서 발생할 수 있는 모든 예외 상황을 정의한다.
 */
public enum TransactionErrorType {
    TRANSACTION_NOT_FOUND("거래를 찾을 수 없습니다"),
    INVALID_TRANSACTION_AMOUNT("유효하지 않은 거래 금액입니다"),
    DUPLICATE_TRANSACTION("중복된 거래입니다"),
    INVALID_TRANSACTION_STATE("유효하지 않은 거래 상태입니다"),
    INSUFFICIENT_BALANCE("잔액이 부족합니다"),
    INVALID_ACCOUNT_STATE("유효하지 않은 계좌 상태입니다"),
    INVALID_VALUE_DATE("유효하지 않은 가치일입니다"),
    TRANSACTION_ALREADY_VOID("이미 무효화된 거래입니다"),
    DUPLICATE_TRANSFER("중복된 송금입니다"),
    TRANSFER_IN_PROGRESS("송금이 처리 중입니다"),
    LEDGER_NOT_FOUND("송금 원장을 찾을 수 없습니다"),
    INVALID_LEDGER_STATE("유효하지 않은 송금 원장 상태입니다"),
    ACCOUNT_API_FAILURE("계좌 서비스 호출에 실패했습니다"),
    TRANSFER_VALIDATION("송금 검증에 실패했습니다");

    private final String message;

    TransactionErrorType(String message) {
        this.message = message;
    }

    /**
     * 예외 유형의 기본 메시지를 반환한다
     *
     * @return 예외 메시지
     */
    public String getMessage() {
        return message;
    }
}
