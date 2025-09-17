package saviing.bank.transaction.application.event;

import java.time.Instant;

import saviing.bank.transaction.domain.model.TransferStatus;
import saviing.bank.transaction.domain.vo.TransferId;

/**
 * 송금 처리 실패 시 발행되는 도메인 이벤트.
 */
public record TransferFailedEvent(
    TransferId transferId,
    TransferStatus status,
    String reason,
    Throwable cause,
    Instant failedAt
) {
}
