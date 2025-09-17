package saviing.bank.transaction.exception;

import java.util.Map;

import saviing.bank.transaction.domain.vo.TransactionId;

/**
 * 거래를 찾을 수 없음 예외
 * 요청된 거래 ID로 거래를 찾을 수 없을 때 발생한다.
 */
public class TransactionNotFoundException extends TransactionException {

    /**
     * 거래 ID로 거래를 찾을 수 없음 예외를 생성한다
     *
     * @param transactionId 찾을 수 없는 거래 ID
     */
    public TransactionNotFoundException(TransactionId transactionId) {
        super(TransactionErrorType.TRANSACTION_NOT_FOUND,
              Map.of("transactionId", transactionId.value()));
    }

    /**
     * 사용자 정의 메시지로 거래를 찾을 수 없음 예외를 생성한다
     *
     * @param message 사용자 정의 에러 메시지
     */
    public TransactionNotFoundException(String message) {
        super(TransactionErrorType.TRANSACTION_NOT_FOUND, message);
    }

    /**
     * 컨텍스트 정보로 거래를 찾을 수 없음 예외를 생성한다
     *
     * @param context 추가 컨텍스트 정보
     */
    public TransactionNotFoundException(Map<String, Object> context) {
        super(TransactionErrorType.TRANSACTION_NOT_FOUND, context);
    }
}