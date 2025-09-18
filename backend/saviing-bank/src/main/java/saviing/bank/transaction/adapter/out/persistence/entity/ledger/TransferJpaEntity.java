package saviing.bank.transaction.adapter.out.persistence.entity.ledger;

import java.time.Instant;
import java.time.LocalDate;
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

import saviing.bank.transaction.domain.model.transfer.TransferStatus;
import saviing.bank.transaction.domain.model.transfer.TransferType;
import saviing.common.annotation.ExecutionTime;

/**
 * Transfer 애그리거트를 저장하기 위한 JPA 엔티티.
 */
@ExecutionTime
@Entity
@Table(
    name = "transfer",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_transfer_source_idem", columnNames = {"source_account_id", "idempotency_key"})
    },
    indexes = {
        @Index(name = "idx_transfer_status", columnList = "status"),
        @Index(name = "idx_transfer_source_account", columnList = "source_account_id"),
        @Index(name = "idx_transfer_target_account", columnList = "target_account_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type", nullable = false, length = 32)
    private TransferType transferType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private TransferStatus status;

    @Column(name = "source_account_id", nullable = false)
    private Long sourceAccountId;

    @Column(name = "target_account_id", nullable = false)
    private Long targetAccountId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "KRW";

    @Column(name = "value_date", nullable = false)
    private LocalDate valueDate;

    @Column(name = "idempotency_key", nullable = false, length = 64)
    private String idempotencyKey;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(
        mappedBy = "transfer",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<LedgerEntryJpaEntity> entries = new ArrayList<>();

    public static TransferJpaEntity create() {
        return new TransferJpaEntity();
    }

    public void addEntry(LedgerEntryJpaEntity entry) {
        entries.add(entry);
        entry.setTransfer(this);
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

    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public void setTargetAccountId(Long targetAccountId) {
        this.targetAccountId = targetAccountId;
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
