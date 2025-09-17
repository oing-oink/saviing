package saviing.bank.transaction.adapter.out.persistence.entity.ledger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import saviing.bank.transaction.domain.model.TransferStatus;
import saviing.bank.transaction.domain.model.TransferType;
import saviing.common.annotation.ExecutionTime;

/**
 * LedgerPair 애그리거트를 저장하기 위한 JPA 엔티티.
 */
@ExecutionTime
@Entity
@Table(
    name = "ledger_pair",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_idempotency_key", columnNames = "idempotency_key")
    },
    indexes = {
        @Index(name = "idx_ledger_pair_status", columnList = "status")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerPairJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_pair_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type", nullable = false, length = 32)
    private TransferType transferType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private TransferStatus status;

    @Column(name = "idempotency_key", nullable = false, length = 64)
    private String idempotencyKey;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(
        mappedBy = "ledgerPair",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<LedgerEntryJpaEntity> entries = new ArrayList<>();

    public static LedgerPairJpaEntity create() {
        return new LedgerPairJpaEntity();
    }

    public void addEntry(LedgerEntryJpaEntity entry) {
        entries.add(entry);
        entry.setLedgerPair(this);
    }

    public void replaceEntries(List<LedgerEntryJpaEntity> newEntries) {
        entries.clear();
        newEntries.forEach(this::addEntry);
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
