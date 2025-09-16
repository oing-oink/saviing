package saviing.bank.transaction.exception;

import java.util.Map;

public class InsufficientBalanceException extends TransactionException {

    public InsufficientBalanceException() {
        super(TransactionErrorType.INSUFFICIENT_BALANCE);
    }

    public InsufficientBalanceException(Map<String, Object> context) {
        super(TransactionErrorType.INSUFFICIENT_BALANCE, context);
    }

    public InsufficientBalanceException(String message) {
        super(TransactionErrorType.INSUFFICIENT_BALANCE, message);
    }

    public InsufficientBalanceException(String message, Map<String, Object> context) {
        super(TransactionErrorType.INSUFFICIENT_BALANCE, message, context);
    }
}