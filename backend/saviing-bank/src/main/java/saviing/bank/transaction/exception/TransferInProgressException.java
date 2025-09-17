package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 송금 처리 중 예외
 * 동일 송금이 아직 처리 중일 때 재요청을 차단하기 위한 예외이다.
 */
public class TransferInProgressException extends TransactionException {

    /**
     * 컨텍스트 정보로 송금 처리 중 예외를 생성한다
     *
     * @param context 추가 컨텍스트 정보
     */
    public TransferInProgressException(Map<String, Object> context) {
        super(TransactionErrorType.TRANSFER_IN_PROGRESS, context);
    }
}
