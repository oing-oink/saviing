package saviing.bank.transaction.adapter.out.persistence.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.adapter.out.persistence.entity.ledger.LedgerEntryJpaEntity;
import saviing.bank.transaction.adapter.out.persistence.entity.ledger.TransferJpaEntity;
import saviing.bank.transaction.domain.model.transfer.LedgerEntry;
import saviing.bank.transaction.domain.model.transfer.Transfer;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;

/**
 * Transfer/LedgerEntry 도메인 객체와 JPA 엔티티 간 변환을 담당하는 매퍼.
 */
@Component
public class LedgerMapper {

    public Transfer toDomain(TransferJpaEntity entity) {
        List<LedgerEntry> entries = entity.getEntries().stream()
            .map(this::toDomainEntry)
            .toList();

        return Transfer.restore(
            entity.getTransferType(),
            entity.getStatus(),
            entity.getIdempotencyKey() != null ? IdempotencyKey.of(entity.getIdempotencyKey()) : null,
            entity.getSourceAccountId(),
            entity.getTargetAccountId(),
            MoneyWon.of(entity.getAmount()),
            entity.getValueDate(),
            entries,
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getFailureReason()
        );
    }

    public TransferJpaEntity toEntity(Transfer ledgerPair) {
        TransferJpaEntity entity = TransferJpaEntity.create();
        entity.setTransferType(ledgerPair.getTransferType());
        entity.setStatus(ledgerPair.getStatus());
        entity.setCreatedAt(ledgerPair.getCreatedAt());
        entity.setUpdatedAt(ledgerPair.getUpdatedAt());
        entity.setFailureReason(ledgerPair.getFailureReason());
        entity.setSourceAccountId(ledgerPair.getSourceAccountId());
        entity.setTargetAccountId(ledgerPair.getTargetAccountId());
        entity.setAmount(ledgerPair.getAmount().amount());
        entity.setValueDate(ledgerPair.getValueDate());
        if (ledgerPair.getIdempotencyKey() != null) {
            entity.setIdempotencyKey(ledgerPair.getIdempotencyKey().value());
        }
        ledgerPair.getEntries().stream()
            .map(this::toEntityEntry)
            .forEach(entity::addEntry);
        return entity;
    }

    public void updateEntity(Transfer ledgerPair, TransferJpaEntity entity) {
        entity.setStatus(ledgerPair.getStatus());
        entity.setUpdatedAt(ledgerPair.getUpdatedAt());
        entity.setFailureReason(ledgerPair.getFailureReason());
        entity.setSourceAccountId(ledgerPair.getSourceAccountId());
        entity.setTargetAccountId(ledgerPair.getTargetAccountId());
        entity.setAmount(ledgerPair.getAmount().amount());
        entity.setValueDate(ledgerPair.getValueDate());
        List<LedgerEntryJpaEntity> updated = ledgerPair.getEntries().stream()
            .map(entry -> updateOrCreateEntry(entity, entry))
            .toList();
        entity.replaceEntries(updated);
    }

    private LedgerEntry toDomainEntry(LedgerEntryJpaEntity entity) {
        return LedgerEntry.restore(
            entity.getId(),
            entity.getAccountId(),
            entity.getDirection(),
            MoneyWon.of(entity.getAmount()),
            entity.getStatus(),
            entity.getValueDate(),
            entity.getPostedAt(),
            entity.getTransactionId() != null ? TransactionId.of(entity.getTransactionId()) : null,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    private LedgerEntryJpaEntity toEntityEntry(LedgerEntry entry) {
        LedgerEntryJpaEntity entity = LedgerEntryJpaEntity.create();
        populateEntryFields(entry, entity);
        return entity;
    }

    private LedgerEntryJpaEntity updateOrCreateEntry(TransferJpaEntity pairEntity, LedgerEntry entry) {
        return pairEntity.getEntries().stream()
            .filter(existing -> existing.getDirection() == entry.getDirection())
            .findFirst()
            .map(existing -> {
                populateEntryFields(entry, existing);
                return existing;
            })
            .orElseGet(() -> {
                LedgerEntryJpaEntity created = LedgerEntryJpaEntity.create();
                populateEntryFields(entry, created);
                created.setTransfer(pairEntity);
                return created;
            });
    }

    private void populateEntryFields(LedgerEntry entry, LedgerEntryJpaEntity entity) {
        entity.setAccountId(entry.getAccountId());
        entity.setDirection(entry.getDirection());
        entity.setAmount(entry.getAmount().amount());
        entity.setStatus(entry.getStatus());
        entity.setValueDate(entry.getValueDate());
        entity.setPostedAt(entry.getPostedAt());
        entity.setTransactionId(entry.getTransactionId() != null ? entry.getTransactionId().value() : null);
        entity.setCreatedAt(entry.getCreatedAt());
        entity.setUpdatedAt(entry.getUpdatedAt());
    }
}
