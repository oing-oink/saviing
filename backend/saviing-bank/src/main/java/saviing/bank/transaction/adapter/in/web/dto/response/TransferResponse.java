package saviing.bank.transaction.adapter.in.web.dto.response;

import java.time.Instant;

import saviing.bank.transaction.application.port.in.result.TransferResult;

/**
 * 송금 처리 결과를 REST 응답으로 제공하기 위한 DTO.
 */
public record TransferResponse(
    Long debitTransactionId,
    Long creditTransactionId,
    String status,
    Instant completedAt
) {

    /**
     * 애플리케이션 결과를 응답 DTO로 변환한다.
     */
    public static TransferResponse from(TransferResult result) {
        return new TransferResponse(
            result.debitTransactionId() != null ? result.debitTransactionId().value() : null,
            result.creditTransactionId() != null ? result.creditTransactionId().value() : null,
            result.status().name(),
            result.completedAt()
        );
    }
}
