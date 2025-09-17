package saviing.bank.transaction.domain.model;

import java.time.Instant;
import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.vo.TransactionId;

/**
 * 거래 내역을 나타내는 도메인 엔티티
 * 계좌의 모든 거래(입금, 출금, 이체 등)를 추적하고 관리한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {

    private TransactionId id;
    private Long accountId;
    private TransactionType transactionType;
    private TransactionDirection direction;
    private MoneyWon amount;
    private LocalDate valueDate;
    private Instant postedAt;
    private TransactionStatus status = TransactionStatus.POSTED;
    private TransactionId relatedTransactionId;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * 거래 엔티티의 생성자
     *
     * @param accountId 계좌 ID
     * @param transactionType 거래 유형
     * @param direction 거래 방향 (입금/출금)
     * @param amount 거래 금액
     * @param valueDate 거래일
     * @param postedAt 거래 처리 시각
     * @param description 거래 설명
     */
    private Transaction(
        @NonNull Long accountId,
        @NonNull TransactionType transactionType,
        @NonNull TransactionDirection direction,
        @NonNull MoneyWon amount,
        @NonNull LocalDate valueDate,
        @NonNull Instant postedAt,
        String description
    ) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.direction = direction;
        this.amount = amount;
        this.valueDate = valueDate;
        this.postedAt = postedAt;
        this.description = description;
        this.createdAt = postedAt;
        this.updatedAt = postedAt;
    }

    /**
     * 새로운 거래를 생성하는 팩토리 메서드
     *
     * @param accountId 계좌 ID
     * @param transactionType 거래 유형
     * @param direction 거래 방향 (입금/출금)
     * @param amount 거래 금액
     * @param valueDate 거래일
     * @param postedAt 거래 처리 시각
     * @param description 거래 설명
     * @return 생성된 거래 엔티티
     */
    public static Transaction create(
        @NonNull Long accountId,
        @NonNull TransactionType transactionType,
        @NonNull TransactionDirection direction,
        @NonNull MoneyWon amount,
        @NonNull LocalDate valueDate,
        @NonNull Instant postedAt,
        String description
    ) {
        validateTransactionTypeAndDirection(transactionType, direction);
        return new Transaction(
            accountId, transactionType, direction, amount,
            valueDate, postedAt, description
        );
    }

    /**
     * 저장된 데이터에서 거래 엔티티를 복원하는 팩토리 메서드
     *
     * @param id 거래 ID
     * @param accountId 계좌 ID
     * @param transactionType 거래 유형
     * @param direction 거래 방향 (입금/출금)
     * @param amount 거래 금액
     * @param valueDate 거래일
     * @param postedAt 거래 처리 시각
     * @param status 거래 상태
     * @param relatedTransactionId 연관 거래 ID
     * @param description 거래 설명
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 거래 엔티티
     */
    public static Transaction restore(
        @NonNull TransactionId id,
        @NonNull Long accountId,
        @NonNull TransactionType transactionType,
        @NonNull TransactionDirection direction,
        @NonNull MoneyWon amount,
        @NonNull LocalDate valueDate,
        @NonNull Instant postedAt,
        @NonNull TransactionStatus status,
        TransactionId relatedTransactionId,
        String description,
        @NonNull Instant createdAt,
        @NonNull Instant updatedAt
    ) {
        Transaction transaction = new Transaction(
            accountId, transactionType, direction, amount,
            valueDate, postedAt, description
        );
        transaction.id = id;
        transaction.status = status;
        transaction.relatedTransactionId = relatedTransactionId;
        transaction.createdAt = createdAt;
        transaction.updatedAt = updatedAt;
        return transaction;
    }

    /**
     * 거래를 무효화(취소) 처리한다
     *
     * @param voidedAt 무효화 처리 시각
     * @throws saviing.bank.transaction.exception.TransactionAlreadyVoidException 이미 무효화된 거래인 경우
     */
    public void voidTransaction(@NonNull Instant voidedAt) {
        if (status.isVoid()) {
            throw new saviing.bank.transaction.exception.TransactionAlreadyVoidException(
                java.util.Map.of("transactionId", id != null ? id.value() : "null"));
        }
        this.status = TransactionStatus.VOID;
        this.updatedAt = voidedAt;
    }

    /**
     * 연관 거래 ID를 설정한다
     *
     * @param relatedTransactionId 연관 거래 ID
     */
    public void setRelatedTransaction(@NonNull TransactionId relatedTransactionId) {
        this.relatedTransactionId = relatedTransactionId;
        this.updatedAt = Instant.now();
    }

    /**
     * 이 거래가 계좌 잔액에 미치는 영향을 계산한다
     * CREDIT은 양수, DEBIT은 음수로 반환한다
     *
     * @return 잔액 변동 금액 (무효화된 거래는 0원)
     */
    public MoneyWon getBalanceImpact() {
        if (status.isVoid()) {
            return MoneyWon.zero();
        }
        // CREDIT(+), DEBIT(-) 개념 반영
        return direction == TransactionDirection.CREDIT ?
            amount : MoneyWon.forBalanceImpact(-amount.amount());
    }

    /**
     * 지정된 거래 ID와 연관된 거래인지 확인한다
     *
     * @param transactionId 확인할 거래 ID
     * @return 연관 거래이면 true, 아니면 false
     */
    public boolean isRelatedTo(TransactionId transactionId) {
        return relatedTransactionId != null && relatedTransactionId.equals(transactionId);
    }

    /**
     * 거래 유형과 방향의 유효성을 검증한다
     *
     * @param transactionType 거래 유형
     * @param direction 거래 방향
     * @throws saviing.bank.transaction.exception.InvalidTransactionStateException 거래 유형과 방향이 맞지 않는 경우
     */
    private static void validateTransactionTypeAndDirection(
        TransactionType transactionType,
        TransactionDirection direction
    ) {
        if (transactionType == TransactionType.REVERSAL) {
            return;
        }

        TransactionDirection expectedDirection = TransactionDirection.from(transactionType);
        if (expectedDirection != direction) {
            throw new saviing.bank.transaction.exception.InvalidTransactionStateException(
                String.format("%s 거래 유형은 %s 방향이어야 합니다",
                    transactionType.name(), expectedDirection.name()),
                java.util.Map.of(
                    "transactionType", transactionType.name(),
                    "expectedDirection", expectedDirection.name(),
                    "actualDirection", direction.name()
                )
            );
        }
    }
}
