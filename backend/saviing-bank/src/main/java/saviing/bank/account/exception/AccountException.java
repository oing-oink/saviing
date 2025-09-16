package saviing.bank.account.exception;

import java.util.Map;

public abstract class AccountException extends RuntimeException {

    private final AccountErrorType errorType;
    private final Map<String, Object> context;

    protected AccountException(AccountErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.context = Map.of();
    }

    protected AccountException(AccountErrorType errorType, Map<String, Object> context) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.context = Map.copyOf(context);
    }

    public AccountErrorType getErrorType() {
        return errorType;
    }

    public Map<String, Object> getContext() {
        return context;
    }
}