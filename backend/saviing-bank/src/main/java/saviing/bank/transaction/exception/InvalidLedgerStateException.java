package saviing.bank.transaction.exception;

/**
 * 유효하지 않은 원장 상태 예외
 * 허용되지 않은 Ledger 상태 전이가 시도될 때 발생하는 예외이다.
 */
public class InvalidLedgerStateException extends TransactionException {

    /**
     * 메시지로 유효하지 않은 원장 상태 예외를 생성한다
     *
     * @param message 에러 메시지
     */
    public InvalidLedgerStateException(String message) {
        super(TransactionErrorType.INVALID_LEDGER_STATE, message);
    }
}
