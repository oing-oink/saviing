package saviing.bank.transaction.domain.model;

/**
 * 거래 유형을 나타내는 열거형
 * 이체, 이자, 취소 등의 거래 종류를 정의한다.
 */
public enum TransactionType {
    TRANSFER_OUT("이체 출금"),
    TRANSFER_IN("이체 입금"),
    INTEREST("이자"),
    REVERSAL("취소");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    /**
     * 거래 유형의 설명을 반환한다
     *
     * @return 거래 유형 설명
     */
    public String getDescription() {
        return description;
    }
}
