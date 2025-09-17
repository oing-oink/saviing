package saviing.bank.transaction.adapter.out.persistence.entity.ledger;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.ledger.LedgerEntryStatus;
import saviing.common.annotation.ExecutionTime;

/**
 * LedgerEntry 애그리거트를 저장하기 위한 JPA 엔티티.
 */
@ExecutionTime
@Entity
@Table(
    name = "ledger_entry",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_transfer_direction", columnNames = {"ledger_pair_id", "direction"})
    },
    indexes = {
        @Index(name = "idx_ledger_entry_account", columnList = "account_id"),
        @Index(name = "idx_ledger_entry_status", columnList = "status")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerEntryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_entry_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ledger_pair_id", nullable = false)
    private LedgerPairJpaEntity ledgerPair;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 16)
    private TransactionDirection direction;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "KRW";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private LedgerEntryStatus status;

    @Column(name = "value_date", nullable = false)
    private LocalDate valueDate;

    @Column(name = "posted_at")
    private Instant postedAt;

    @Column(name = "idempotency_key", length = 64)
    private String idempotencyKey;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static LedgerEntryJpaEntity create() {
        return new LedgerEntryJpaEntity();
    }

    public void setLedgerPair(LedgerPairJpaEntity ledgerPair) {
        this.ledgerPair = ledgerPair;
    }

    public void setStatus(LedgerEntryStatus status) {
        this.status = status;
    }

    public void setPostedAt(Instant postedAt) {
        this.postedAt = postedAt;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setDirection(TransactionDirection direction) {
        this.direction = direction;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
