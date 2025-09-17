package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 동일 멱등 키로 이미 처리된 송금이 존재할 때 발생하는 예외.
 */
public class DuplicateTransferRequestException extends TransactionException {

    public DuplicateTransferRequestException(Map<String, Object> context) {
        super(TransactionErrorType.DUPLICATE_TRANSFER, context);
    }
}
