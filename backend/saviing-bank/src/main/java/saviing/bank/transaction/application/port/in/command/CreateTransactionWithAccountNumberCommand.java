package saviing.bank.transaction.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.NonNull;

import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.TransactionType;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.common.vo.MoneyWon;

@Builder
public record CreateTransactionWithAccountNumberCommand(
    @NonNull String accountNumber,
    @NonNull TransactionType transactionType,
    @NonNull TransactionDirection direction,
    @NonNull MoneyWon amount,
    @NonNull LocalDate valueDate,
    IdempotencyKey idempotencyKey,
    String description
) {

    /**
     * 웹 요청 파라미터로부터 CreateTransactionWithAccountNumberCommand를 생성하는 팩토리 메서드
     *
     * @param accountNumber 계좌번호
     * @param transactionType 거래 유형
     * @param direction 거래 방향
     * @param amount 거래 금액
     * @param valueDate 가치일
     * @param idempotencyKey 멱등키 (null 가능)
     * @param description 거래 설명 (null 가능)
     * @return CreateTransactionWithAccountNumberCommand 인스턴스
     */
    public static CreateTransactionWithAccountNumberCommand of(
        String accountNumber,
        String transactionType,
        String direction,
        BigDecimal amount,
        LocalDate valueDate,
        String idempotencyKey,
        String description
    ) {
        return new CreateTransactionWithAccountNumberCommand(
            accountNumber,
            TransactionType.valueOf(transactionType),
            TransactionDirection.valueOf(direction),
            MoneyWon.of(amount),
            valueDate,
            idempotencyKey != null ? IdempotencyKey.of(idempotencyKey) : null,
            description
        );
    }
}