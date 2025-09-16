package saviing.bank.transaction.application.port.in.result;

import java.time.Instant;
import java.time.LocalDate;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionResult(
    Long transactionId,
    Long accountId,
    String transactionType,
    String direction,
    BigDecimal amount,
    LocalDate valueDate,
    Instant postedAt,
    String status,
    Long relatedTransactionId,
    String idempotencyKey,
    String description,
    Instant createdAt,
    Instant updatedAt
) {

    /**
     * Transaction 도메인 객체로부터 TransactionResult를 생성하는 팩토리 메서드
     *
     * @param transaction Transaction 도메인 객체
     * @return TransactionResult 인스턴스
     */
    public static TransactionResult from(saviing.bank.transaction.domain.model.Transaction transaction) {
        return TransactionResult.builder()
            .transactionId(transaction.getId() != null ? transaction.getId().value() : null)
            .accountId(transaction.getAccountId())
            .transactionType(transaction.getTransactionType().name())
            .direction(transaction.getDirection().name())
            .amount(BigDecimal.valueOf(transaction.getAmount().amount()))
            .valueDate(transaction.getValueDate())
            .postedAt(transaction.getPostedAt())
            .status(transaction.getStatus().name())
            .relatedTransactionId(transaction.getRelatedTransactionId() != null ? transaction.getRelatedTransactionId().value() : null)
            .idempotencyKey(transaction.getIdempotencyKey() != null ? transaction.getIdempotencyKey().value() : null)
            .description(transaction.getDescription())
            .createdAt(transaction.getCreatedAt())
            .updatedAt(transaction.getUpdatedAt())
            .build();
    }
}