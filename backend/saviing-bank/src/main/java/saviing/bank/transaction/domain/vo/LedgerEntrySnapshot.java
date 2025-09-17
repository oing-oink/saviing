package saviing.bank.transaction.domain.vo;

import java.time.Instant;
import java.time.LocalDate;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.transfer.LedgerEntryStatus;

/**
 * LedgerEntry 읽기 전용 뷰 모델.
 */
public record LedgerEntrySnapshot(
    Long ledgerEntryId,
    Long accountId,
    TransactionDirection direction,
    MoneyWon amount,
    LedgerEntryStatus status,
    LocalDate valueDate,
    Instant postedAt,
    IdempotencyKey idempotencyKey,
    TransactionId transactionId,
    Instant createdAt,
    Instant updatedAt
) {
}
