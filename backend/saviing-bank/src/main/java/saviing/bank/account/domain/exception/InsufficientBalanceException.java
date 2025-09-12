package saviing.bank.account.domain.exception;

public class InsufficientBalanceException extends DomainException {
    
    public InsufficientBalanceException() {
        super(DomainErrorCode.INSUFFICIENT_BALANCE);
    }
    
    public InsufficientBalanceException(String accountNumber, long requestAmount, long currentBalance) {
        super(DomainErrorCode.INSUFFICIENT_BALANCE, 
            String.format("잔액이 부족합니다. 계좌: %s, 요청금액: %d원, 현재잔액: %d원", 
                accountNumber, requestAmount, currentBalance));
    }
}