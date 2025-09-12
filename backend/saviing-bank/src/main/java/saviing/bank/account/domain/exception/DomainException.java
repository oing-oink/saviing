package saviing.bank.account.domain.exception;

public abstract class DomainException extends RuntimeException {
    
    private final AccountErrorCode errorCode;
    
    protected DomainException(AccountErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    protected DomainException(AccountErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected DomainException(AccountErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    protected DomainException(AccountErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public AccountErrorCode getErrorCode() {
        return errorCode;
    }
}