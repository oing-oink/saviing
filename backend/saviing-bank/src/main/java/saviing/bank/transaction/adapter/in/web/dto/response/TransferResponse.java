package saviing.bank.transaction.adapter.in.web.dto.response;

import java.time.Instant;
import java.time.LocalDate;

import saviing.bank.transaction.application.port.in.result.TransferResult;

/**
 * 송금 처리 결과를 REST 응답으로 제공하기 위한 DTO.
 */
public record TransferResponse(
    String idempotencyKey,
    Long sourceAccountId,
    Long targetAccountId,
    Long amount,
    LocalDate valueDate,
    String status,
    Instant requestedAt,
    Instant completedAt,
    String failureReason
) {

    /**
     * 애플리케이션 결과를 응답 DTO로 변환한다.
     */
    public static TransferResponse from(TransferResult result) {
        return new TransferResponse(
            result.idempotencyKey() != null ? result.idempotencyKey().value() : null,
            result.sourceAccountId(),
            result.targetAccountId(),
            result.amount() != null ? result.amount().amount() : null,
            result.valueDate(),
            result.status().name(),
            result.requestedAt(),
            result.completedAt(),
            result.failureReason()
        );
    }
}
