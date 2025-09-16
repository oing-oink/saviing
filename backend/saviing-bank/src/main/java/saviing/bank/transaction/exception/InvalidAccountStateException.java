package saviing.bank.transaction.exception;

import java.util.Map;

public class InvalidAccountStateException extends TransactionException {

    public InvalidAccountStateException() {
        super(TransactionErrorType.INVALID_ACCOUNT_STATE);
    }

    public InvalidAccountStateException(Map<String, Object> context) {
        super(TransactionErrorType.INVALID_ACCOUNT_STATE, context);
    }

    public InvalidAccountStateException(String message) {
        super(TransactionErrorType.INVALID_ACCOUNT_STATE, message);
    }

    public InvalidAccountStateException(String message, Map<String, Object> context) {
        super(TransactionErrorType.INVALID_ACCOUNT_STATE, message, context);
    }
}