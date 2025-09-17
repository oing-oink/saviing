package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 중복 송금 요청 예외
 * 동일 멱등 키로 이미 처리된 송금이 존재할 때 발생하는 예외이다.
 */
public class DuplicateTransferRequestException extends TransactionException {

    /**
     * 컨텍스트 정보로 중복 송금 요청 예외를 생성한다
     *
     * @param context 추가 컨텍스트 정보
     */
    public DuplicateTransferRequestException(Map<String, Object> context) {
        super(TransactionErrorType.DUPLICATE_TRANSFER, context);
    }
}
