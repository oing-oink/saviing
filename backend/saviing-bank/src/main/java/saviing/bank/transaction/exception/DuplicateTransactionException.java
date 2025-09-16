package saviing.bank.transaction.exception;

import java.util.Map;

import saviing.bank.transaction.domain.vo.IdempotencyKey;

public class DuplicateTransactionException extends TransactionException {

    public DuplicateTransactionException(Long accountId, IdempotencyKey idempotencyKey) {
        super(TransactionErrorType.DUPLICATE_TRANSACTION,
              Map.of("accountId", accountId, "idempotencyKey", idempotencyKey.value()));
    }

    public DuplicateTransactionException(String message, Long accountId, IdempotencyKey idempotencyKey) {
        super(TransactionErrorType.DUPLICATE_TRANSACTION, message,
              Map.of("accountId", accountId, "idempotencyKey", idempotencyKey.value()));
    }

    public DuplicateTransactionException(Map<String, Object> context) {
        super(TransactionErrorType.DUPLICATE_TRANSACTION, context);
    }
}