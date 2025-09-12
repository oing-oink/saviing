package saviing.bank.account.domain.model;

public enum CompoundingType {
    SIMPLE("단리"),
    DAILY("일복리"),
    MONTH("월복리"),
    YEAR("연복리");
    
    private final String description;
    
    CompoundingType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}