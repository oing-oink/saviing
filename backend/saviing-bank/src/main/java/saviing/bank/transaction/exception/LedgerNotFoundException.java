package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 원장을 찾을 수 없음 예외
 * 요청한 IdempotencyKey에 해당하는 LedgerPair가 없을 때 발생하는 예외이다.
 */
public class LedgerNotFoundException extends TransactionException {

    /**
     * 컨텍스트 정보로 원장을 찾을 수 없음 예외를 생성한다
     *
     * @param context 추가 컨텍스트 정보
     */
    public LedgerNotFoundException(Map<String, Object> context) {
        super(TransactionErrorType.LEDGER_NOT_FOUND, context);
    }
}
