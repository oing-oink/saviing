package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 유효하지 않은 계좌 상태 예외
 * 계좌이 거래를 수행할 수 없는 상태일 때 발생한다.
 */
public class InvalidAccountStateException extends TransactionException {

    /**
     * 기본 유효하지 않은 계좌 상태 예외를 생성한다
     */
    public InvalidAccountStateException() {
        super(TransactionErrorType.INVALID_ACCOUNT_STATE);
    }

    /**
     * 컨텍스트 정보와 함께 유효하지 않은 계좌 상태 예외를 생성한다
     *
     * @param context 추가 컨텍스트 정보
     */
    public InvalidAccountStateException(Map<String, Object> context) {
        super(TransactionErrorType.INVALID_ACCOUNT_STATE, context);
    }

    /**
     * 사용자 정의 메시지와 함께 유효하지 않은 계좌 상태 예외를 생성한다
     *
     * @param message 사용자 정의 에러 메시지
     */
    public InvalidAccountStateException(String message) {
        super(TransactionErrorType.INVALID_ACCOUNT_STATE, message);
    }

    /**
     * 메시지와 컨텍스트 정보로 유효하지 않은 계좌 상태 예외를 생성한다
     *
     * @param message 사용자 정의 에러 메시지
     * @param context 추가 컨텍스트 정보
     */
    public InvalidAccountStateException(String message, Map<String, Object> context) {
        super(TransactionErrorType.INVALID_ACCOUNT_STATE, message, context);
    }
}