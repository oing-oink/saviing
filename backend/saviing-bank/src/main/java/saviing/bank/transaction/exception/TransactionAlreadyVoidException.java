package saviing.bank.transaction.exception;

import java.util.Map;

public class TransactionAlreadyVoidException extends TransactionException {

    public TransactionAlreadyVoidException() {
        super(TransactionErrorType.TRANSACTION_ALREADY_VOID);
    }

    public TransactionAlreadyVoidException(Map<String, Object> context) {
        super(TransactionErrorType.TRANSACTION_ALREADY_VOID, context);
    }

    public TransactionAlreadyVoidException(String message) {
        super(TransactionErrorType.TRANSACTION_ALREADY_VOID, message);
    }

    public TransactionAlreadyVoidException(String message, Map<String, Object> context) {
        super(TransactionErrorType.TRANSACTION_ALREADY_VOID, message, context);
    }
}