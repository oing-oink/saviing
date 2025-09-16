package saviing.bank.transaction.exception;

import java.util.Map;

public abstract class TransactionException extends RuntimeException {

    private final TransactionErrorType errorType;
    private final Map<String, Object> context;

    protected TransactionException(TransactionErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.context = Map.of();
    }

    protected TransactionException(TransactionErrorType errorType, Map<String, Object> context) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.context = Map.copyOf(context);
    }

    protected TransactionException(TransactionErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
        this.context = Map.of();
    }

    protected TransactionException(TransactionErrorType errorType, String message, Map<String, Object> context) {
        super(message);
        this.errorType = errorType;
        this.context = Map.copyOf(context);
    }

    public TransactionErrorType getErrorType() {
        return errorType;
    }

    public Map<String, Object> getContext() {
        return context;
    }
}