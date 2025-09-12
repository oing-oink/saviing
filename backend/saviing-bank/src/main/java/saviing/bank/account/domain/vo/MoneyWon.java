package saviing.bank.account.domain.vo;

import java.math.BigDecimal;

public record MoneyWon(long amount) {
    
    public MoneyWon {
        if (amount < 0) {
            throw new IllegalArgumentException("금액은 음수일 수 없습니다: " + amount);
        }
    }
    
    public static MoneyWon of(long amount) {
        return new MoneyWon(amount);
    }
    
    public static MoneyWon zero() {
        return new MoneyWon(0L);
    }
    
    public MoneyWon add(MoneyWon other) {
        return new MoneyWon(this.amount + other.amount);
    }
    
    public MoneyWon subtract(MoneyWon other) {
        long result = this.amount - other.amount;
        if (result < 0) {
            throw new IllegalArgumentException("결과가 음수가 될 수 없습니다");
        }
        return new MoneyWon(result);
    }
    
    public MoneyWon multiply(BigDecimal multiplier) {
        BigDecimal result = BigDecimal.valueOf(amount).multiply(multiplier);
        return new MoneyWon(result.longValue());
    }
    
    public boolean isGreaterThan(MoneyWon other) {
        return this.amount > other.amount;
    }
    
    public boolean isGreaterThanOrEqual(MoneyWon other) {
        return this.amount >= other.amount;
    }
    
    public boolean isLessThan(MoneyWon other) {
        return this.amount < other.amount;
    }
    
    public boolean isZero() {
        return this.amount == 0;
    }
    
    public boolean isPositive() {
        return this.amount > 0;
    }
    
    @Override
    public String toString() {
        return amount + "원";
    }
}