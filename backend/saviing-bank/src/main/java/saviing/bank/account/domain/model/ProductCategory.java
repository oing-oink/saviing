package saviing.bank.account.domain.model;

public enum ProductCategory {
    DEMAND_DEPOSIT("요구불예금"),
    TIME_DEPOSIT("정기예금"),
    INSTALLMENT_SAVINGS("적금");

    private final String description;

    ProductCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}