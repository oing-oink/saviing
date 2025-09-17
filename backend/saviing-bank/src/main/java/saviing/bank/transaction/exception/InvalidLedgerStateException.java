package saviing.bank.transaction.exception;

/**
 * 허용되지 않은 Ledger 상태 전이가 시도될 때 발생하는 예외.
 */
public class InvalidLedgerStateException extends TransactionException {

    public InvalidLedgerStateException(String message) {
        super(TransactionErrorType.INVALID_LEDGER_STATE, message);
    }
}
