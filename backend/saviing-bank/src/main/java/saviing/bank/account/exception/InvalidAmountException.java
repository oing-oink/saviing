package saviing.bank.account.exception;

import java.util.Map;

public class InvalidAmountException extends AccountException {

    public InvalidAmountException() {
        super(AccountErrorType.INVALID_AMOUNT);
    }

    public InvalidAmountException(Map<String, Object> context) {
        super(AccountErrorType.INVALID_AMOUNT, context);
    }
}