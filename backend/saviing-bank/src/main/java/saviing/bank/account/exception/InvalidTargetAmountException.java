package saviing.bank.account.exception;

import java.util.Map;

public class InvalidTargetAmountException extends AccountException {

    public InvalidTargetAmountException() {
        super(AccountErrorType.INVALID_TARGET_AMOUNT);
    }

    public InvalidTargetAmountException(Map<String, Object> context) {
        super(AccountErrorType.INVALID_TARGET_AMOUNT, context);
    }
}