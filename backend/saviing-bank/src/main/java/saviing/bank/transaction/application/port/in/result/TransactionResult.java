package saviing.bank.transaction.application.port.in.result;

import java.time.Instant;
import java.time.LocalDate;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * 거래 조회 결과를 나타내는 DTO
 * 거래 도메인 엔티티의 정보를 클라이언트에게 전달하기 위한 형태로 변환한다.
 */
@Builder
public record TransactionResult(
    Long transactionId,
    Long accountId,
    String transactionType,
    String direction,
    BigDecimal amount,
    BigDecimal balanceAfter,
    LocalDate valueDate,
    Instant postedAt,
    String status,
    Long relatedTransactionId,
    String description,
    Instant createdAt,
    Instant updatedAt
) {

    /**
     * Transaction 도메인 객체로부터 TransactionResult를 생성하는 팩토리 메서드
     * 도메인 엔티티를 DTO로 변환하여 외부 계층에서 사용할 수 있도록 한다.
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
            .balanceAfter(BigDecimal.valueOf(transaction.getBalanceAfter().amount()))
            .valueDate(transaction.getValueDate())
            .postedAt(transaction.getPostedAt())
            .status(transaction.getStatus().name())
            .relatedTransactionId(transaction.getRelatedTransactionId() != null ? transaction.getRelatedTransactionId().value() : null)
            .description(transaction.getDescription())
            .createdAt(transaction.getCreatedAt())
            .updatedAt(transaction.getUpdatedAt())
            .build();
    }
}
