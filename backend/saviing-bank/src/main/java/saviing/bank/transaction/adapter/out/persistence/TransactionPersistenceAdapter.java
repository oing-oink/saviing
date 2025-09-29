package saviing.bank.transaction.adapter.out.persistence;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import saviing.bank.transaction.adapter.out.persistence.entity.TransactionJpaEntity;
import saviing.bank.transaction.adapter.out.persistence.repository.JpaTransactionRepository;
import saviing.bank.transaction.application.port.out.LoadTransactionPort;
import saviing.bank.transaction.application.port.out.SaveTransactionPort;
import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.bank.transaction.exception.TransactionNotFoundException;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements LoadTransactionPort, SaveTransactionPort {

    private final JpaTransactionRepository repository;

    @Override
    public Optional<Transaction> loadTransaction(TransactionId transactionId) {
        return repository.findById(transactionId.value())
            .map(TransactionJpaEntity::toDomain);
    }

    @Override
    public List<Transaction> loadTransactionsByAccount(Long accountId) {
        return repository.findByAccountIdOrderByPostedAtDesc(accountId)
            .stream()
            .map(TransactionJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<Transaction> loadTransactionsByAccount(Long accountId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return repository.findByAccountIdOrderByPostedAtDesc(accountId, pageRequest)
            .stream()
            .map(TransactionJpaEntity::toDomain)
            .toList();
    }

    @Override
    public TransactionId saveTransaction(Transaction transaction) {
        TransactionJpaEntity entity = TransactionJpaEntity.fromDomain(transaction);
        TransactionJpaEntity saved = repository.save(entity);
        return TransactionId.of(saved.getTxnId());
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        TransactionJpaEntity entity = repository.findById(transaction.getId().value())
            .orElseThrow(() -> new TransactionNotFoundException(
                Map.of("transactionId", transaction.getId().value())
            ));

        entity.updateFromDomain(transaction);
        repository.save(entity);
    }
}
