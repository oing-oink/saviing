package saviing.bank.common.vo;

import java.math.BigDecimal;
import java.util.Objects;

public final class MoneyWon {

    private final long amount;

    private MoneyWon(long amount) {
        this.amount = amount;
    }

    private MoneyWon(long amount, boolean allowNegative) {
        if (!allowNegative && amount < 0) {
            throw new IllegalArgumentException("금액은 음수일 수 없습니다: " + amount);
        }
        this.amount = amount;
    }

    public static MoneyWon of(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("금액은 음수일 수 없습니다: " + amount);
        }
        return new MoneyWon(amount);
    }

    public static MoneyWon of(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("금액은 null일 수 없습니다");
        }
        long longValue = amount.longValue();
        if (longValue < 0) {
            throw new IllegalArgumentException("금액은 음수일 수 없습니다: " + longValue);
        }
        return new MoneyWon(longValue);
    }

    public static MoneyWon zero() {
        return new MoneyWon(0L);
    }

    public static MoneyWon forBalanceImpact(long amount) {
        return new MoneyWon(amount, true);
    }

    public long amount() {
        return amount;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MoneyWon moneyWon = (MoneyWon) obj;
        return amount == moneyWon.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}