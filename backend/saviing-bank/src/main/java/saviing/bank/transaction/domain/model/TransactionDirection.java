package saviing.bank.transaction.domain.model;

public enum TransactionDirection {
    CREDIT("잔액 증가"),
    DEBIT("잔액 감소");

    private final String description;

    TransactionDirection(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static TransactionDirection from(TransactionType transactionType) {
        return switch (transactionType) {
            case DEPOSIT, TRANSFER_IN, INTEREST -> CREDIT;
            case WITHDRAWAL, TRANSFER_OUT, FEE -> DEBIT;
            case REVERSAL, ADJUSTMENT -> throw new IllegalArgumentException(
                "REVERSAL과 ADJUSTMENT는 원거래의 Direction에 따라 결정되어야 합니다"
            );
        };
    }
}