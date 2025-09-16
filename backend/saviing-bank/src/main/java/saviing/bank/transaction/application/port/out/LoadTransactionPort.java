package saviing.bank.transaction.application.port.out;

import java.util.List;
import java.util.Optional;

import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;

public interface LoadTransactionPort {

    Optional<Transaction> loadTransaction(TransactionId transactionId);

    List<Transaction> loadTransactionsByAccount(Long accountId);

    List<Transaction> loadTransactionsByAccount(Long accountId, int page, int size);

    Optional<Transaction> loadTransactionByIdempotencyKey(Long accountId, IdempotencyKey idempotencyKey);

    boolean existsByIdempotencyKey(Long accountId, IdempotencyKey idempotencyKey);
}