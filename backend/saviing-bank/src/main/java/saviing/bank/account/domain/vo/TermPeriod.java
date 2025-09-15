package saviing.bank.account.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import saviing.bank.account.domain.model.TermUnit;

/**
 * 금융상품의 기간을 나타내는 값 객체
 *
 * 기간 값과 단위를 함께 관리하여 타입 안전성을 보장하며,
 * 다양한 기간 단위 간의 변환과 비교 기능을 제공합니다.
 * 예: 26주, 12개월, 2년 등의 기간을 표현
 */

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