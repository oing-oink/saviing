package saviing.bank.account.exception;

import java.util.Map;

public class InvalidSavingsTermException extends AccountException {

    public InvalidSavingsTermException() {
        super(AccountErrorType.INVALID_SAVINGS_TERM);
    }

    public InvalidSavingsTermException(Map<String, Object> context) {
        super(AccountErrorType.INVALID_SAVINGS_TERM, context);
    }
}