package saviing.bank.transaction.exception;

import java.util.Map;

import saviing.bank.transaction.domain.model.TransactionStatus;
import saviing.bank.transaction.domain.vo.TransactionId;

public class InvalidTransactionStateException extends TransactionException {

    public InvalidTransactionStateException(TransactionId transactionId, TransactionStatus currentStatus) {
        super(TransactionErrorType.INVALID_TRANSACTION_STATE,
              Map.of("transactionId", transactionId.value(), "currentStatus", currentStatus));
    }

    public InvalidTransactionStateException(String message, TransactionId transactionId, TransactionStatus currentStatus) {
        super(TransactionErrorType.INVALID_TRANSACTION_STATE, message,
              Map.of("transactionId", transactionId.value(), "currentStatus", currentStatus));
    }

    public InvalidTransactionStateException(Map<String, Object> context) {
        super(TransactionErrorType.INVALID_TRANSACTION_STATE, context);
    }

    public InvalidTransactionStateException(String message, Map<String, Object> context) {
        super(TransactionErrorType.INVALID_TRANSACTION_STATE, message, context);
    }
}