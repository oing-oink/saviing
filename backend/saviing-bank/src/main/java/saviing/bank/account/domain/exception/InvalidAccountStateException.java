package saviing.bank.account.domain.exception;

import saviing.bank.account.domain.model.AccountStatus;

public class InvalidAccountStateException extends DomainException {
    
    public InvalidAccountStateException(String message) {
        super(DomainErrorCode.INVALID_ACCOUNT_STATE, message);
    }
    
    public InvalidAccountStateException(AccountStatus currentStatus, String operation) {
        super(DomainErrorCode.INVALID_ACCOUNT_STATE, 
            String.format("현재 계좌 상태(%s)에서는 %s 작업을 수행할 수 없습니다", 
                currentStatus.getDescription(), operation));
    }
    
    public InvalidAccountStateException(String accountNumber, AccountStatus currentStatus, String operation) {
        super(DomainErrorCode.INVALID_ACCOUNT_STATE, 
            String.format("계좌 %s의 현재 상태(%s)에서는 %s 작업을 수행할 수 없습니다", 
                accountNumber, currentStatus.getDescription(), operation));
    }
}