package saviing.bank.transaction.domain.vo;

import java.time.Instant;
import java.util.List;

import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.transfer.TransferStatus;
import saviing.bank.transaction.domain.model.transfer.TransferType;

/**
 * Transfer의 상태를 조회하기 위한 스냅샷 모델.
 */
public record TransferSnapshot(
    TransferType transferType,
    TransferStatus status,
    IdempotencyKey idempotencyKey,
    List<LedgerEntrySnapshot> entries,
    Instant createdAt,
    Instant updatedAt,
    String failureReason
) {

    /**
     * 차변(출금) 엔트리를 반환한다.
     */
    public LedgerEntrySnapshot debitEntry() {
        return entries.stream()
            .filter(entry -> entry.direction() == TransactionDirection.DEBIT)
            .findFirst()
            .orElse(null);
    }

    /**
     * 대변(입금) 엔트리를 반환한다.
     */
    public LedgerEntrySnapshot creditEntry() {
        return entries.stream()
            .filter(entry -> entry.direction() == TransactionDirection.CREDIT)
            .findFirst()
            .orElse(null);
    }
}
