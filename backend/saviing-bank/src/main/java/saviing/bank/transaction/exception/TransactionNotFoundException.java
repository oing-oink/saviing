package saviing.bank.transaction.exception;

import java.util.Map;

import saviing.bank.transaction.domain.vo.TransactionId;

public class TransactionNotFoundException extends TransactionException {

    public TransactionNotFoundException(TransactionId transactionId) {
        super(TransactionErrorType.TRANSACTION_NOT_FOUND,
              Map.of("transactionId", transactionId.value()));
    }

    public TransactionNotFoundException(String message) {
        super(TransactionErrorType.TRANSACTION_NOT_FOUND, message);
    }

    public TransactionNotFoundException(Map<String, Object> context) {
        super(TransactionErrorType.TRANSACTION_NOT_FOUND, context);
    }
}