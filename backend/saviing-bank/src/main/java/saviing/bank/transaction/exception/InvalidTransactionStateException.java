package saviing.bank.transaction.exception;

import java.util.Map;

import saviing.bank.transaction.domain.model.TransactionStatus;
import saviing.bank.transaction.domain.vo.TransactionId;

/**
 * 유효하지 않은 거래 상태 예외
 * 거래의 상태가 요청된 작업을 수행할 수 없는 상태일 때 발생한다.
 */
public class InvalidTransactionStateException extends TransactionException {

    /**
     * 거래 ID와 현재 상태로 예외를 생성한다
     *
     * @param transactionId 거래 ID
     * @param currentStatus 현재 거래 상태
     */
    public InvalidTransactionStateException(TransactionId transactionId, TransactionStatus currentStatus) {
        super(TransactionErrorType.INVALID_TRANSACTION_STATE,
              Map.of("transactionId", transactionId.value(), "currentStatus", currentStatus));
    }

    /**
     * 메시지, 거래 ID, 상태로 예외를 생성한다
     *
     * @param message 사용자 정의 에러 메시지
     * @param transactionId 거래 ID
     * @param currentStatus 현재 거래 상태
     */
    public InvalidTransactionStateException(String message, TransactionId transactionId, TransactionStatus currentStatus) {
        super(TransactionErrorType.INVALID_TRANSACTION_STATE, message,
              Map.of("transactionId", transactionId.value(), "currentStatus", currentStatus));
    }

    /**
     * 컨텍스트 정보로 예외를 생성한다
     *
     * @param context 추가 컨텍스트 정보
     */
    public InvalidTransactionStateException(Map<String, Object> context) {
        super(TransactionErrorType.INVALID_TRANSACTION_STATE, context);
    }

    /**
     * 메시지와 컨텍스트 정보로 예외를 생성한다
     *
     * @param message 사용자 정의 에러 메시지
     * @param context 추가 컨텍스트 정보
     */
    public InvalidTransactionStateException(String message, Map<String, Object> context) {
        super(TransactionErrorType.INVALID_TRANSACTION_STATE, message, context);
    }
}