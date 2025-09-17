package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 동일 송금이 아직 처리 중일 때 재요청을 차단하기 위한 예외.
 */
public class TransferInProgressException extends TransactionException {

    public TransferInProgressException(Map<String, Object> context) {
        super(TransactionErrorType.TRANSFER_IN_PROGRESS, context);
    }
}
