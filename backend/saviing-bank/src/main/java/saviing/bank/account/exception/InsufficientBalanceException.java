package saviing.bank.account.exception;

import java.util.Map;

public class InsufficientBalanceException extends AccountException {

    public InsufficientBalanceException() {
        super(AccountErrorType.INSUFFICIENT_BALANCE);
    }

    public InsufficientBalanceException(Map<String, Object> context) {
        super(AccountErrorType.INSUFFICIENT_BALANCE, context);
    }
}