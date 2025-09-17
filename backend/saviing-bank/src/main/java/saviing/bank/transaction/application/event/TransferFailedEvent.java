package saviing.bank.transaction.application.event;

import java.time.Instant;

import saviing.bank.transaction.domain.model.TransferStatus;
import saviing.bank.transaction.domain.vo.IdempotencyKey;

/**
 * 송금 처리 실패 시 발행되는 도메인 이벤트.
 */
public record TransferFailedEvent(
    IdempotencyKey idempotencyKey,
    TransferStatus status,
    String reason,
    Throwable cause,
    Instant failedAt
) {
}
