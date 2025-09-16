package saviing.bank.transaction.exception;

import java.util.Map;

public class InvalidValueDateException extends TransactionException {

    public InvalidValueDateException() {
        super(TransactionErrorType.INVALID_VALUE_DATE);
    }

    public InvalidValueDateException(Map<String, Object> context) {
        super(TransactionErrorType.INVALID_VALUE_DATE, context);
    }

    public InvalidValueDateException(String message) {
        super(TransactionErrorType.INVALID_VALUE_DATE, message);
    }

    public InvalidValueDateException(String message, Map<String, Object> context) {
        super(TransactionErrorType.INVALID_VALUE_DATE, message, context);
    }
}