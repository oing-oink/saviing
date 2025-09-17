package saviing.bank.transaction.application.port.in.result;

import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;

import saviing.bank.transaction.domain.model.TransferStatus;
import saviing.bank.transaction.domain.vo.TransactionId;

/**
 * 송금 처리 결과를 표현하는 DTO.
 * 생성된 송금 ID, 연관 거래 ID, 처리 상태 및 완료 시각 정보를 포함한다.
 */
@Builder
public record TransferResult(
    TransactionId debitTransactionId,
    TransactionId creditTransactionId,
    TransferStatus status,
    Instant completedAt
) {
}
