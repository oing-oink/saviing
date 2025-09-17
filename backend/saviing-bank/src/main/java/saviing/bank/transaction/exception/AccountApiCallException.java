package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * AccountInternalApi 호출 실패를 나타내는 예외.
 */
public class AccountApiCallException extends TransactionException {

    public AccountApiCallException(String message, Map<String, Object> context) {
        super(TransactionErrorType.ACCOUNT_API_FAILURE, message, context);
    }
}
