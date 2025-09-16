package saviing.bank.transaction.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.vo.TransactionId;

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
    private IdempotencyKey idempotencyKey;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    private Transaction(
        @NonNull Long accountId,
        @NonNull TransactionType transactionType,
        @NonNull TransactionDirection direction,
        @NonNull MoneyWon amount,
        @NonNull LocalDate valueDate,
        @NonNull Instant postedAt,
        IdempotencyKey idempotencyKey,
        String description
    ) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.direction = direction;
        this.amount = amount;
        this.valueDate = valueDate;
        this.postedAt = postedAt;
        this.idempotencyKey = idempotencyKey;
        this.description = description;
        this.createdAt = postedAt;
        this.updatedAt = postedAt;
    }

    public static Transaction create(
        @NonNull Long accountId,
        @NonNull TransactionType transactionType,
        @NonNull TransactionDirection direction,
        @NonNull MoneyWon amount,
        @NonNull LocalDate valueDate,
        @NonNull Instant postedAt,
        IdempotencyKey idempotencyKey,
        String description
    ) {
        validateTransactionTypeAndDirection(transactionType, direction);
        return new Transaction(
            accountId, transactionType, direction, amount,
            valueDate, postedAt, idempotencyKey, description
        );
    }

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
        IdempotencyKey idempotencyKey,
        String description,
        @NonNull Instant createdAt,
        @NonNull Instant updatedAt
    ) {
        Transaction transaction = new Transaction(
            accountId, transactionType, direction, amount,
            valueDate, postedAt, idempotencyKey, description
        );
        transaction.id = id;
        transaction.status = status;
        transaction.relatedTransactionId = relatedTransactionId;
        transaction.createdAt = createdAt;
        transaction.updatedAt = updatedAt;
        return transaction;
    }

    public void voidTransaction(@NonNull Instant voidedAt) {
        if (status.isVoid()) {
            throw new saviing.bank.transaction.exception.TransactionAlreadyVoidException(
                java.util.Map.of("transactionId", id != null ? id.value() : "null"));
        }
        this.status = TransactionStatus.VOID;
        this.updatedAt = voidedAt;
    }

    public void setRelatedTransaction(@NonNull TransactionId relatedTransactionId) {
        this.relatedTransactionId = relatedTransactionId;
        this.updatedAt = Instant.now();
    }

    public MoneyWon getBalanceImpact() {
        if (status.isVoid()) {
            return MoneyWon.zero();
        }
        // CREDIT(+), DEBIT(-) 개념 반영
        return direction == TransactionDirection.CREDIT ?
            amount : MoneyWon.forBalanceImpact(-amount.amount());
    }

    public boolean hasIdempotencyKey() {
        return idempotencyKey != null;
    }

    public boolean isRelatedTo(TransactionId transactionId) {
        return relatedTransactionId != null && relatedTransactionId.equals(transactionId);
    }

    private static void validateTransactionTypeAndDirection(
        TransactionType transactionType,
        TransactionDirection direction
    ) {
        if (transactionType == TransactionType.REVERSAL || transactionType == TransactionType.ADJUSTMENT) {
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