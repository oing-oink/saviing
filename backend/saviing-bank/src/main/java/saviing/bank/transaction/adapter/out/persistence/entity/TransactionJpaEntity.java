package saviing.bank.transaction.adapter.out.persistence.entity;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.TransactionStatus;
import saviing.bank.transaction.domain.model.TransactionType;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.vo.TransactionId;

@Entity
@Table(
    name = "transaction",
    indexes = {
        @Index(name = "idx_account_posted_at", columnList = "account_id, posted_at"),
        @Index(name = "idx_value_date", columnList = "value_date"),
        @Index(name = "idx_related_txn_id", columnList = "related_txn_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "txn_id")
    private Long txnId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "txn_type", nullable = false)
    private TransactionType txnType;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    private TransactionDirection direction;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "value_date", nullable = false)
    private LocalDate valueDate;

    @Column(name = "posted_at", nullable = false)
    private Instant postedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status = TransactionStatus.POSTED;

    @Column(name = "related_txn_id")
    private Long relatedTxnId;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static TransactionJpaEntity fromDomain(Transaction transaction) {
        TransactionJpaEntity entity = new TransactionJpaEntity();
        entity.txnId = transaction.getId() != null ? transaction.getId().value() : null;
        entity.accountId = transaction.getAccountId();
        entity.txnType = transaction.getTransactionType();
        entity.direction = transaction.getDirection();
        entity.amount = transaction.getAmount().amount();
        entity.valueDate = transaction.getValueDate();
        entity.postedAt = transaction.getPostedAt();
        entity.status = transaction.getStatus();
        entity.relatedTxnId = transaction.getRelatedTransactionId() != null
            ? transaction.getRelatedTransactionId().value() : null;
        entity.description = transaction.getDescription();
        entity.createdAt = transaction.getCreatedAt();
        entity.updatedAt = transaction.getUpdatedAt();
        return entity;
    }

    public Transaction toDomain() {
        return Transaction.restore(
            TransactionId.of(txnId),
            accountId,
            txnType,
            direction,
            MoneyWon.of(amount),
            valueDate,
            postedAt,
            status,
            relatedTxnId != null ? TransactionId.of(relatedTxnId) : null,
            description,
            createdAt,
            updatedAt
        );
    }

    public void updateFromDomain(Transaction transaction) {
        this.status = transaction.getStatus();
        this.relatedTxnId = transaction.getRelatedTransactionId() != null
            ? transaction.getRelatedTransactionId().value() : null;
        this.updatedAt = transaction.getUpdatedAt();
    }
}
