package saviing.bank.transaction.domain.model.ledger;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.TransferStatus;
import saviing.bank.transaction.domain.model.TransferType;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.bank.transaction.domain.vo.TransferId;
import saviing.bank.transaction.exception.InvalidLedgerStateException;

/**
 * 송금 단위를 표현하는 애그리거트 루트.
 * 두 개의 LedgerEntry를 묶어 double-entry 불변식을 유지한다.
 */
public class LedgerPair {

    private final TransferId transferId;
    private final TransferType transferType;
    private TransferStatus status;
    private final IdempotencyKey idempotencyKey;
    private final List<LedgerEntry> entries;
    private final Instant createdAt;
    private Instant updatedAt;
    private String failureReason;

    private LedgerPair(
        TransferId transferId,
        TransferType transferType,
        TransferStatus status,
        IdempotencyKey idempotencyKey,
        List<LedgerEntry> entries,
        Instant createdAt,
        Instant updatedAt,
        String failureReason
    ) {
        this.transferId = Objects.requireNonNull(transferId, "transferId must not be null");
        this.transferType = Objects.requireNonNull(transferType, "transferType must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.idempotencyKey = idempotencyKey;
        this.entries = new ArrayList<>(Objects.requireNonNull(entries, "entries must not be null"));
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        this.failureReason = failureReason;
    }

    /**
     * 신규 송금을 초기화할 때 사용되는 팩토리 메서드.
     */
    public static LedgerPair create(
        TransferId transferId,
        Long sourceAccountId,
        Long targetAccountId,
        MoneyWon amount,
        LocalDate valueDate,
        TransferType transferType,
        IdempotencyKey idempotencyKey,
        Instant now
    ) {
        LedgerEntry debit = LedgerEntry.create(
            sourceAccountId,
            TransactionDirection.DEBIT,
            amount,
            valueDate,
            idempotencyKey,
            now
        );
        LedgerEntry credit = LedgerEntry.create(
            targetAccountId,
            TransactionDirection.CREDIT,
            amount,
            valueDate,
            idempotencyKey,
            now
        );
        return new LedgerPair(
            transferId,
            transferType,
            TransferStatus.REQUESTED,
            idempotencyKey,
            List.of(debit, credit),
            now,
            now,
            null
        );
    }

    /**
     * 저장소에서 읽어온 LedgerPair를 복원한다.
     */
    public static LedgerPair restore(
        TransferId transferId,
        TransferType transferType,
        TransferStatus status,
        IdempotencyKey idempotencyKey,
        List<LedgerEntry> entries,
        Instant createdAt,
        Instant updatedAt,
        String failureReason
    ) {
        return new LedgerPair(transferId, transferType, status, idempotencyKey, entries, createdAt, updatedAt, failureReason);
    }

    public TransferId getTransferId() {
        return transferId;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public IdempotencyKey getIdempotencyKey() {
        return idempotencyKey;
    }

    public List<LedgerEntry> getEntries() {
        return List.copyOf(entries);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    /**
     * 특정 방향(Debit/Credit)의 엔트리를 조회한다.
     */
    public LedgerEntry getEntry(TransactionDirection direction) {
        return entries.stream()
            .filter(entry -> entry.getDirection() == direction)
            .findFirst()
            .orElseThrow(() -> new InvalidLedgerStateException("Ledger entry not found for direction " + direction));
    }

    /**
     * 지정된 방향의 엔트리가 POSTED 상태가 되었음을 반영한다.
     */
    public void markEntryPosted(TransactionDirection direction, TransactionId transactionId, Instant postedAt) {
        LedgerEntry entry = getEntry(direction);
        entry.markPosted(transactionId, postedAt);
        this.updatedAt = postedAt;
        if (direction == TransactionDirection.DEBIT) {
            this.status = TransferStatus.DEBIT_POSTED;
        } else {
            this.status = TransferStatus.CREDIT_POSTED;
        }
    }

    /**
     * 송금이 실패했을 때 상태와 실패 사유를 기록한다.
     */
    public void markFailed(String reason, Instant failedAt) {
        this.failureReason = reason;
        this.status = TransferStatus.FAILED;
        entries.stream()
            .filter(entry -> entry.getStatus() != LedgerEntryStatus.POSTED)
            .forEach(entry -> entry.markFailed(failedAt));
        this.updatedAt = failedAt;
    }

    /**
     * 두 엔트리가 모두 POSTED 상태일 때 송금을 정산 완료로 전환한다.
     */
    public void markSettled(Instant settledAt) {
        boolean allPosted = entries.stream().allMatch(entry -> entry.getStatus() == LedgerEntryStatus.POSTED);
        if (!allPosted) {
            throw new InvalidLedgerStateException("Cannot settle transfer before both entries are posted");
        }
        this.status = TransferStatus.SETTLED;
        this.updatedAt = settledAt;
    }

    /**
     * 조회용 스냅샷으로 변환한다.
     */
    public LedgerPairSnapshot toSnapshot() {
        List<LedgerEntrySnapshot> snapshots = entries.stream()
            .map(LedgerEntry::toSnapshot)
            .toList();
        return new LedgerPairSnapshot(
            transferId,
            transferType,
            status,
            idempotencyKey,
            snapshots,
            createdAt,
            updatedAt,
            failureReason
        );
    }
}
