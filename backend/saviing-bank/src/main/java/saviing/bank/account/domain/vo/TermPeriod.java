package saviing.bank.account.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import saviing.bank.account.domain.model.TermUnit;

@Getter
@EqualsAndHashCode
public class TermPeriod {

    private final int value;
    private final TermUnit unit;

    private TermPeriod(int value, @NonNull TermUnit unit) {
        if (value <= 0) {
            throw new IllegalArgumentException("기간 값은 양수여야 합니다: " + value);
        }
        this.value = value;
        this.unit = unit;
    }

    public static TermPeriod of(int value, TermUnit unit) {
        return new TermPeriod(value, unit);
    }

    public static TermPeriod weeks(int weeks) {
        return new TermPeriod(weeks, TermUnit.WEEKS);
    }

    public static TermPeriod months(int months) {
        return new TermPeriod(months, TermUnit.MONTHS);
    }

    public static TermPeriod years(int years) {
        return new TermPeriod(years, TermUnit.YEARS);
    }

    public int toDays() {
        return unit.toDays(value);
    }

    public int toWeeks() {
        return toDays() / TermUnit.WEEKS.getDaysMultiplier();
    }

    public int toMonths() {
        return toDays() / TermUnit.MONTHS.getDaysMultiplier();
    }

    public boolean isLongerThan(TermPeriod other) {
        return this.toDays() > other.toDays();
    }

    public boolean isShorterThan(TermPeriod other) {
        return this.toDays() < other.toDays();
    }

    @Override
    public String toString() {
        return value + unit.getDescription();
    }
}