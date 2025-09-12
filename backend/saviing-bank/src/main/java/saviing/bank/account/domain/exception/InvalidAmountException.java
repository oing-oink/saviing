package saviing.bank.account.domain.exception;

public class InvalidAmountException extends DomainException {
    
    public InvalidAmountException(String message) {
        super(AccountErrorCode.INVALID_AMOUNT, message);
    }
    
    public InvalidAmountException(long amount) {
        super(AccountErrorCode.INVALID_AMOUNT, 
            String.format("유효하지 않은 금액입니다: %d원", amount));
    }
    
    public static InvalidAmountException negativeAmount(long amount) {
        return new InvalidAmountException(String.format("금액은 0 이상이어야 합니다: %d원", amount));
    }
    
    public static InvalidAmountException zeroAmount() {
        return new InvalidAmountException("금액은 0보다 커야 합니다");
    }
}