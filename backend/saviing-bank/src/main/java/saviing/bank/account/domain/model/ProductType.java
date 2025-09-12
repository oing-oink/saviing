package saviing.bank.account.domain.model;

public enum ProductType {
    DEMAND_DEPOSIT("요구불 예금"),
    INSTALLMENT_SAVINGS("적금");
    
    private final String description;
    
    ProductType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}