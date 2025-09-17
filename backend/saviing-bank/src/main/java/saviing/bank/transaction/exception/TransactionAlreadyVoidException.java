package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 이미 무효화된 거래 예외
 * 이미 무횤화(취소)된 거래에 대해 다시 무효화를 시도할 때 발생한다.
 */
public class TransactionAlreadyVoidException extends TransactionException {

    /**
     * 기본 이미 무효화된 거래 예외를 생성한다
     */
    public TransactionAlreadyVoidException() {
        super(TransactionErrorType.TRANSACTION_ALREADY_VOID);
    }

    /**
     * 컨텍스트 정보와 함께 이미 무효화된 거래 예외를 생성한다
     *
     * @param context 추가 컨텍스트 정보
     */
    public TransactionAlreadyVoidException(Map<String, Object> context) {
        super(TransactionErrorType.TRANSACTION_ALREADY_VOID, context);
    }

    /**
     * 사용자 정의 메시지와 함께 이미 무효화된 거래 예외를 생성한다
     *
     * @param message 사용자 정의 에러 메시지
     */
    public TransactionAlreadyVoidException(String message) {
        super(TransactionErrorType.TRANSACTION_ALREADY_VOID, message);
    }

    /**
     * 메시지와 컨텍스트 정보로 이미 무효화된 거래 예외를 생성한다
     *
     * @param message 사용자 정의 에러 메시지
     * @param context 추가 컨텍스트 정보
     */
    public TransactionAlreadyVoidException(String message, Map<String, Object> context) {
        super(TransactionErrorType.TRANSACTION_ALREADY_VOID, message, context);
    }
}