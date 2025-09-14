package saviing.bank.account.exception;

import java.util.Map;

public class InvalidAccountStateException extends AccountException {

    public InvalidAccountStateException() {
        super(AccountErrorType.INVALID_ACCOUNT_STATE);
    }

    public InvalidAccountStateException(Map<String, Object> context) {
        super(AccountErrorType.INVALID_ACCOUNT_STATE, context);
    }
}