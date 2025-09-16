package saviing.bank.transaction.domain.model;

public enum TransactionStatus {
    POSTED("정상 처리"),
    VOID("무효화/취소");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPosted() {
        return this == POSTED;
    }

    public boolean isVoid() {
        return this == VOID;
    }
}