package saviing.bank.transaction.domain.model;

/**
 * 거래 상태를 나타내는 열거형
 * 거래의 유효성과 처리 상태를 관리한다.
 */
public enum TransactionStatus {
    POSTED("정상 처리"),
    VOID("무효화/취소");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    /**
     * 거래 상태의 설명을 반환한다
     *
     * @return 상태 설명
     */
    public String getDescription() {
        return description;
    }

    /**
     * 정상 처리된 거래인지 확인한다
     *
     * @return 정상 처리된 거래이면 true, 아니면 false
     */
    public boolean isPosted() {
        return this == POSTED;
    }

    /**
     * 무효화(취소)된 거래인지 확인한다
     *
     * @return 무효화된 거래이면 true, 아니면 false
     */
    public boolean isVoid() {
        return this == VOID;
    }
}