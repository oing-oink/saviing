package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 계좌 서비스 API 호출 실패 예외
 * AccountInternalApi 호출 실패를 나타내는 예외이다.
 */
public class AccountApiCallException extends TransactionException {

    /**
     * 메시지와 컨텍스트 정보로 계좌 API 호출 실패 예외를 생성한다
     *
     * @param message 에러 메시지
     * @param context 추가 컨텍스트 정보
     */
    public AccountApiCallException(String message, Map<String, Object> context) {
        super(TransactionErrorType.ACCOUNT_API_FAILURE, message, context);
    }
}
