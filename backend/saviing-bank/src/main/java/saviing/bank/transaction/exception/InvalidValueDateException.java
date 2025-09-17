package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 유효하지 않은 가치일 예외
 * 거래의 가치일이 유효하지 않을 때 발생한다 (예: 미래일, 영업일 아닌 날).
 */
public class InvalidValueDateException extends TransactionException {

    /**
     * 기본 유효하지 않은 가치일 예외를 생성한다
     */
    public InvalidValueDateException() {
        super(TransactionErrorType.INVALID_VALUE_DATE);
    }

    /**
     * 컨텍스트 정보와 함께 유효하지 않은 가치일 예외를 생성한다
     *
     * @param context 추가 컨텍스트 정보
     */
    public InvalidValueDateException(Map<String, Object> context) {
        super(TransactionErrorType.INVALID_VALUE_DATE, context);
    }

    /**
     * 사용자 정의 메시지와 함께 유효하지 않은 가치일 예외를 생성한다
     *
     * @param message 사용자 정의 에러 메시지
     */
    public InvalidValueDateException(String message) {
        super(TransactionErrorType.INVALID_VALUE_DATE, message);
    }

    /**
     * 메시지와 컨텍스트 정보로 유효하지 않은 가치일 예외를 생성한다
     *
     * @param message 사용자 정의 에러 메시지
     * @param context 추가 컨텍스트 정보
     */
    public InvalidValueDateException(String message, Map<String, Object> context) {
        super(TransactionErrorType.INVALID_VALUE_DATE, message, context);
    }
}