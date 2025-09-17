package saviing.bank.transaction.application.event;

import java.time.Instant;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.model.TransferType;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.bank.transaction.domain.vo.IdempotencyKey;

/**
 * 송금이 정상적으로 완료되었을 때 발행되는 도메인 이벤트.
 */
public record TransferSettledEvent(
    IdempotencyKey idempotencyKey,
    TransactionId debitTransactionId,
    TransactionId creditTransactionId,
    MoneyWon amount,
    TransferType transferType,
    Instant settledAt
) {
}
