package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 송금 검증 예외
 * 송금 전 비즈니스 검증에 실패했을 때 발생하는 예외이다.
 */
public class TransferValidationException extends TransactionException {

    /**
     * 메시지와 컨텍스트 정보로 송금 검증 예외를 생성한다
     *
     * @param message 검증 실패 메시지
     * @param context 추가 컨텍스트 정보
     */
    public TransferValidationException(String message, Map<String, Object> context) {
        super(TransactionErrorType.TRANSFER_VALIDATION, message, context);
    }
}
