package saviing.bank.transaction.application.port.in.result;

import java.time.Instant;
import java.time.LocalDate;

import lombok.Builder;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.model.transfer.TransferStatus;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;

/**
 * 송금 처리 결과를 표현하는 DTO.
 * 멱등 키, 계좌/금액, 상태 및 거래 정보 등을 포함해 클라이언트가 송금 상태를 추적할 수 있도록 한다.
 */
@Builder
public record TransferResult(
    IdempotencyKey idempotencyKey,
    Long sourceAccountId,
    Long targetAccountId,
    MoneyWon amount,
    LocalDate valueDate,
    TransferStatus status,
    Instant requestedAt,
    Instant completedAt,
    String failureReason,
    TransactionId debitTransactionId,
    TransactionId creditTransactionId
) {
}
