package saviing.bank.account.domain.exception;

public abstract class DomainException extends RuntimeException {
    
    private final DomainErrorCode errorCode;
    
    protected DomainException(DomainErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    protected DomainException(DomainErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected DomainException(DomainErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    protected DomainException(DomainErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public DomainErrorCode getErrorCode() {
        return errorCode;
    }
}