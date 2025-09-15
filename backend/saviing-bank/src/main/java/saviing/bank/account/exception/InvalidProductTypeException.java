package saviing.bank.account.exception;

import java.util.Map;

public class InvalidProductTypeException extends AccountException {

    public InvalidProductTypeException() {
        super(AccountErrorType.INVALID_PRODUCT_TYPE);
    }

    public InvalidProductTypeException(Map<String, Object> context) {
        super(AccountErrorType.INVALID_PRODUCT_TYPE, context);
    }
}