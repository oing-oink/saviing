package saviing.bank.transaction.exception;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 유효하지 않은 거래 금액 예외
 * 거래 금액이 유효하지 않을 때 발생한다 (예: 음수, 0, 범위 초과).
 */
public class InvalidTransactionAmountException extends TransactionException {

    /**
     * 유효하지 않은 금액으로 예외를 생성한다
     *
     * @param amount 유효하지 않은 거래 금액
     */
    public InvalidTransactionAmountException(BigDecimal amount) {
        super(TransactionErrorType.INVALID_TRANSACTION_AMOUNT,
              Map.of("amount", amount));
    }

    /**
     * 메시지와 금액으로 예외를 생성한다
     *
     * @param message 사용자 정의 에러 메시지
     * @param amount 유효하지 않은 거래 금액
     */
    public InvalidTransactionAmountException(String message, BigDecimal amount) {
        super(TransactionErrorType.INVALID_TRANSACTION_AMOUNT, message,
              Map.of("amount", amount));
    }

    /**
     * 컨텍스트 정보로 예외를 생성한다
     *
     * @param context 추가 컨텍스트 정보
     */
    public InvalidTransactionAmountException(Map<String, Object> context) {
        super(TransactionErrorType.INVALID_TRANSACTION_AMOUNT, context);
    }
}