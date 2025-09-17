package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 잔액 부족 예외
 * 계좌 잔액이 거래 금액보다 적을 때 발생한다.
 */
public class InsufficientBalanceException extends TransactionException {

    /**
     * 기본 잔액 부족 예외를 생성한다
     */
    public InsufficientBalanceException() {
        super(TransactionErrorType.INSUFFICIENT_BALANCE);
    }

    /**
     * 컨텍스트 정보와 함께 잔액 부족 예외를 생성한다
     *
     * @param context 추가 컨텍스트 정보
     */
    public InsufficientBalanceException(Map<String, Object> context) {
        super(TransactionErrorType.INSUFFICIENT_BALANCE, context);
    }

    /**
     * 사용자 정의 메시지와 함께 잔액 부족 예외를 생성한다
     *
     * @param message 사용자 정의 에러 메시지
     */
    public InsufficientBalanceException(String message) {
        super(TransactionErrorType.INSUFFICIENT_BALANCE, message);
    }

    /**
     * 메시지와 컨텍스트 정보로 잔액 부족 예외를 생성한다
     *
     * @param message 사용자 정의 에러 메시지
     * @param context 추가 컨텍스트 정보
     */
    public InsufficientBalanceException(String message, Map<String, Object> context) {
        super(TransactionErrorType.INSUFFICIENT_BALANCE, message, context);
    }
}