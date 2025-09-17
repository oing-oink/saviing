package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 거래 도메인의 기본 예외 클래스
 * 모든 거래 관련 예외의 부모 클래스로써 에러 유형과 컨텍스트를 관리한다.
 */
public abstract class TransactionException extends RuntimeException {

    private final TransactionErrorType errorType;
    private final Map<String, Object> context;

    /**
     * 에러 유형만으로 예외를 생성한다
     *
     * @param errorType 예외 유형
     */
    protected TransactionException(TransactionErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.context = Map.of();
    }

    /**
     * 에러 유형과 컨텍스트로 예외를 생성한다
     *
     * @param errorType 예외 유형
     * @param context 추가 컨텍스트 정보
     */
    protected TransactionException(TransactionErrorType errorType, Map<String, Object> context) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.context = Map.copyOf(context);
    }

    /**
     * 에러 유형과 사용자 정의 메시지로 예외를 생성한다
     *
     * @param errorType 예외 유형
     * @param message 사용자 정의 에러 메시지
     */
    protected TransactionException(TransactionErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
        this.context = Map.of();
    }

    /**
     * 에러 유형, 메시지, 컨텍스트로 예외를 생성한다
     *
     * @param errorType 예외 유형
     * @param message 사용자 정의 에러 메시지
     * @param context 추가 컨텍스트 정보
     */
    protected TransactionException(TransactionErrorType errorType, String message, Map<String, Object> context) {
        super(message);
        this.errorType = errorType;
        this.context = Map.copyOf(context);
    }

    /**
     * 예외의 에러 유형을 반환한다
     *
     * @return 예외 유형
     */
    public TransactionErrorType getErrorType() {
        return errorType;
    }

    /**
     * 예외의 컨텍스트 정보를 반환한다
     *
     * @return 컨텍스트 정보 맵
     */
    public Map<String, Object> getContext() {
        return context;
    }
}