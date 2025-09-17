package saviing.bank.transaction.domain.model;

/**
 * 거래 방향을 나타내는 열거형
 * 계좌 잔액의 증가(입금) 또는 감소(출금)를 나타낸다.
 */
public enum TransactionDirection {
    CREDIT("잔액 증가"),
    DEBIT("잔액 감소");

    private final String description;

    TransactionDirection(String description) {
        this.description = description;
    }

    /**
     * 거래 방향의 설명을 반환한다
     *
     * @return 방향 설명
     */
    public String getDescription() {
        return description;
    }

    /**
     * 거래 유형에 따른 예상 거래 방향을 반환한다
     *
     * @param transactionType 거래 유형
     * @return 해당 거래 유형의 기본 방향
     * @throws IllegalArgumentException REVERSAL 거래 유형인 경우
     */
    public static TransactionDirection from(TransactionType transactionType) {
        return switch (transactionType) {
            case TRANSFER_IN, INTEREST -> CREDIT;
            case TRANSFER_OUT -> DEBIT;
            case REVERSAL -> throw new IllegalArgumentException(
                "REVERSAL은 원거래의 방향에 따라 결정되어야 합니다"
            );
        };
    }
}
