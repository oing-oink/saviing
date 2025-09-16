package saviing.bank.account.exception;

import java.util.Map;

public class InvalidRateException extends AccountException {

    public InvalidRateException() {
        super(AccountErrorType.INVALID_RATE);
    }

    public InvalidRateException(Map<String, Object> context) {
        super(AccountErrorType.INVALID_RATE, context);
    }
}