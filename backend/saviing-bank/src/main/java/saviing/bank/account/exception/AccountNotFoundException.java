package saviing.bank.account.exception;

import java.util.Map;

public class AccountNotFoundException extends AccountException {

    public AccountNotFoundException(Map<String, Object> context) {
        super(AccountErrorType.ACCOUNT_NOT_FOUND, context);
    }
}