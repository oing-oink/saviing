package saviing.bank.transaction.exception;

import java.util.Map;

import saviing.bank.transaction.domain.vo.IdempotencyKey;

/**
 * 중복 거래 예외
 * 동일한 멱등키로 중복된 거래가 시도될 때 발생한다.
 */
public class DuplicateTransactionException extends TransactionException {

    /**
     * 계좌 ID와 멱등키로 중복 거래 예외를 생성한다
     *
     * @param accountId 계좌 ID
     * @param idempotencyKey 멱등키
     */
    public DuplicateTransactionException(Long accountId, IdempotencyKey idempotencyKey) {
        super(TransactionErrorType.DUPLICATE_TRANSACTION,
              Map.of("accountId", accountId, "idempotencyKey", idempotencyKey.value()));
    }

    /**
     * 사용자 정의 메시지와 함께 중복 거래 예외를 생성한다
     *
     * @param message 사용자 정의 에러 메시지
     * @param accountId 계좌 ID
     * @param idempotencyKey 멱등키
     */
    public DuplicateTransactionException(String message, Long accountId, IdempotencyKey idempotencyKey) {
        super(TransactionErrorType.DUPLICATE_TRANSACTION, message,
              Map.of("accountId", accountId, "idempotencyKey", idempotencyKey.value()));
    }

    /**
     * 컨텍스트 정보로 중복 거래 예외를 생성한다
     *
     * @param context 추가 컨텍스트 정보
     */
    public DuplicateTransactionException(Map<String, Object> context) {
        super(TransactionErrorType.DUPLICATE_TRANSACTION, context);
    }
}