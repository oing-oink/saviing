package saviing.bank.account.domain.model;

public enum PaymentCycle {
    DAILY("매일"),
    WEEKLY("매주"),
    MONTHLY("매월"),
    QUARTERLY("분기별"),
    SEMI_ANNUALLY("반기별"),
    ANNUALLY("매년");

    private final String description;

    PaymentCycle(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}