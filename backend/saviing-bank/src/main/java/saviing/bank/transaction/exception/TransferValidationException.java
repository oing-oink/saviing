package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 송금 전 비즈니스 검증에 실패했을 때 발생하는 예외.
 */
public class TransferValidationException extends TransactionException {

    public TransferValidationException(String message, Map<String, Object> context) {
        super(TransactionErrorType.TRANSFER_VALIDATION, message, context);
    }
}
