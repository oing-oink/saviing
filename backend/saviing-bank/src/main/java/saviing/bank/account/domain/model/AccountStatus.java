package saviing.bank.account.domain.model;

public enum AccountStatus {
    ACTIVE("활성"),
    FROZEN("동결"),
    CLOSED("해지");
    
    private final String description;
    
    AccountStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean canTransact() {
        return this == ACTIVE;
    }
    
    public boolean canFreeze() {
        return this == ACTIVE;
    }
    
    public boolean canUnfreeze() {
        return this == FROZEN;
    }
    
    public boolean canClose() {
        return this == ACTIVE || this == FROZEN;
    }
}