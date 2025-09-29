package saviing.bank.transaction.domain.model.transfer;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.bank.transaction.exception.InvalidLedgerStateException;

/**
 * 송금 단위를 표현하는 애그리거트 루트.
 * 두 개의 LedgerEntry를 묶어 double-entry 불변식을 유지한다.
 */
public class Transfer {

    private final TransferType transferType;
    private TransferStatus status;
    private final IdempotencyKey idempotencyKey;
    private final Long sourceAccountId;
    private final Long targetAccountId;
    private final MoneyWon amount;
    private final LocalDate valueDate;
    private final List<LedgerEntry> entries;
    private final Instant createdAt;
    private Instant updatedAt;
    private String failureReason;

    /**
     * Transfer 생성자
     *
     * @param transferType 송금 유형
     * @param status 송금 상태
     * @param idempotencyKey 멱등키
     * @param entries 원장 엔트리 목록
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @param failureReason 실패 사유
     */
    private Transfer(
        TransferType transferType,
        TransferStatus status,
        IdempotencyKey idempotencyKey,
        Long sourceAccountId,
        Long targetAccountId,
        MoneyWon amount,
        LocalDate valueDate,
        List<LedgerEntry> entries,
        Instant createdAt,
        Instant updatedAt,
        String failureReason
    ) {
        this.transferType = Objects.requireNonNull(transferType, "transferType must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.idempotencyKey = idempotencyKey;
        this.sourceAccountId = Objects.requireNonNull(sourceAccountId, "sourceAccountId must not be null");
        this.targetAccountId = Objects.requireNonNull(targetAccountId, "targetAccountId must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.valueDate = Objects.requireNonNull(valueDate, "valueDate must not be null");
        this.entries = new ArrayList<>(Objects.requireNonNull(entries, "entries must not be null"));
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        this.failureReason = failureReason;
        validateEntriesConsistency();
    }

    /**
     * 신규 송금을 초기화할 때 사용되는 팩토리 메서드
     *
     * @param sourceAccountId 출금 계좌 ID
     * @param targetAccountId 입금 계좌 ID
     * @param amount 송금 금액
     * @param valueDate 가치일
     * @param transferType 송금 유형
     * @param idempotencyKey 멱등키
     * @param now 생성 시각
     * @return 생성된 원장 페어
     */
    public static Transfer create(
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
            now
        );
        LedgerEntry credit = LedgerEntry.create(
            targetAccountId,
            TransactionDirection.CREDIT,
            amount,
            valueDate,
            now
        );
        return new Transfer(
            transferType,
            TransferStatus.REQUESTED,
            idempotencyKey,
            sourceAccountId,
            targetAccountId,
            amount,
            valueDate,
            List.of(debit, credit),
            now,
            now,
            null
        );
    }

    /**
     * 저장소에서 읽어온 Transfer를 복원한다
     *
     * @param transferType 송금 유형
     * @param status 송금 상태
     * @param idempotencyKey 멱등키
     * @param sourceAccountId 출금 계좌 ID
     * @param targetAccountId 입금 계좌 ID
     * @param amount 송금 금액
     * @param valueDate 가치일
     * @param entries 원장 엔트리 목록
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @param failureReason 실패 사유
     * @return 복원된 원장 페어
     */
    public static Transfer restore(
        TransferType transferType,
        TransferStatus status,
        IdempotencyKey idempotencyKey,
        Long sourceAccountId,
        Long targetAccountId,
        MoneyWon amount,
        LocalDate valueDate,
        List<LedgerEntry> entries,
        Instant createdAt,
        Instant updatedAt,
        String failureReason
    ) {
        return new Transfer(
            transferType,
            status,
            idempotencyKey,
            sourceAccountId,
            targetAccountId,
            amount,
            valueDate,
            entries,
            createdAt,
            updatedAt,
            failureReason
        );
    }


    /**
     * 송금 유형을 반환한다
     *
     * @return 송금 유형
     */
    public TransferType getTransferType() {
        return transferType;
    }

    /**
     * 출금 계좌 ID를 반환한다
     */
    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    /**
     * 입금 계좌 ID를 반환한다
     */
    public Long getTargetAccountId() {
        return targetAccountId;
    }

    /**
     * 송금 금액을 반환한다
     */
    public MoneyWon getAmount() {
        return amount;
    }

    /**
     * 가치일을 반환한다
     */
    public LocalDate getValueDate() {
        return valueDate;
    }

    /**
     * 송금 상태를 반환한다
     *
     * @return 송금 상태
     */
    public TransferStatus getStatus() {
        return status;
    }

    /**
     * 멱등키를 반환한다
     *
     * @return 멱등키
     */
    public IdempotencyKey getIdempotencyKey() {
        return idempotencyKey;
    }

    /**
     * 원장 엔트리 목록을 반환한다
     *
     * @return 원장 엔트리 목록 (복사본)
     */
    public List<LedgerEntry> getEntries() {
        return List.copyOf(entries);
    }

    /**
     * 생성 시각을 반환한다
     *
     * @return 생성 시각
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * 수정 시각을 반환한다
     *
     * @return 수정 시각
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 실패 사유를 반환한다
     *
     * @return 실패 사유
     */
    public String getFailureReason() {
        return failureReason;
    }

    private void validateEntriesConsistency() {
        if (entries.size() != 2) {
            throw new InvalidLedgerStateException("Transfer must have exactly two ledger entries");
        }

        LedgerEntry debit = getEntry(TransactionDirection.DEBIT);
        LedgerEntry credit = getEntry(TransactionDirection.CREDIT);

        if (!Objects.equals(debit.getAccountId(), sourceAccountId)) {
            throw new InvalidLedgerStateException("Debit entry account does not match sourceAccountId");
        }
        if (!Objects.equals(credit.getAccountId(), targetAccountId)) {
            throw new InvalidLedgerStateException("Credit entry account does not match targetAccountId");
        }

        if (!debit.getAmount().equals(amount) || !credit.getAmount().equals(amount)) {
            throw new InvalidLedgerStateException("Ledger entry amounts do not match transfer amount");
        }

        if (!debit.getValueDate().equals(valueDate) || !credit.getValueDate().equals(valueDate)) {
            throw new InvalidLedgerStateException("Ledger entry value dates do not match transfer value date");
        }
    }

    /**
     * 특정 방향(Debit/Credit)의 엔트리를 조회한다
     *
     * @param direction 거래 방향
     * @return 해당 방향의 원장 엔트리
     * @throws InvalidLedgerStateException 해당 방향의 엔트리를 찾을 수 없는 경우
     */
    public LedgerEntry getEntry(TransactionDirection direction) {
        return entries.stream()
            .filter(entry -> entry.getDirection() == direction)
            .findFirst()
            .orElseThrow(() -> new InvalidLedgerStateException("Ledger entry not found for direction " + direction));
    }

    /**
     * 지정된 방향의 엔트리가 POSTED 상태가 되었음을 반영한다
     *
     * @param direction 거래 방향
     * @param transactionId 거래 ID
     * @param postedAt 처리 시각
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
     * 송금이 실패했을 때 상태와 실패 사유를 기록한다
     *
     * @param reason 실패 사유
     * @param failedAt 실패 시각
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
     * 두 엔트리가 모두 POSTED 상태일 때 송금을 정산 완료로 전환한다
     *
     * @param settledAt 정산 완료 시각
     * @throws InvalidLedgerStateException 모든 엔트리가 POSTED 상태가 아닌 경우
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
     * 조회용 스냅샷으로 변환한다
     *
     * @return 원장 페어 스냅샷
     */
    public saviing.bank.transaction.domain.vo.TransferSnapshot toSnapshot() {
        List<saviing.bank.transaction.domain.vo.LedgerEntrySnapshot> snapshots = entries.stream()
            .map(LedgerEntry::toSnapshot)
            .toList();
        return new saviing.bank.transaction.domain.vo.TransferSnapshot(
            transferType,
            status,
            idempotencyKey,
            sourceAccountId,
            targetAccountId,
            amount,
            valueDate,
            snapshots,
            createdAt,
            updatedAt,
            failureReason
        );
    }
}
