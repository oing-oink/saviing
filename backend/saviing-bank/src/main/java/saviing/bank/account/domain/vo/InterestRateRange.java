package saviing.bank.account.domain.vo;

import java.util.Objects;

public record InterestRateRange(
    BasisPoints minRate,
    BasisPoints maxRate
) {

    public InterestRateRange {
        Objects.requireNonNull(minRate, "최소 이자율은 필수입니다");
        Objects.requireNonNull(maxRate, "최대 이자율은 필수입니다");

        if (minRate.isGreaterThan(maxRate)) {
            throw new IllegalArgumentException("최소 이자율은 최대 이자율보다 클 수 없습니다");
        }
    }

    public static InterestRateRange of(BasisPoints minRate, BasisPoints maxRate) {
        return new InterestRateRange(minRate, maxRate);
    }

    public static InterestRateRange fixed(BasisPoints rate) {
        return new InterestRateRange(rate, rate);
    }

    public static InterestRateRange zero() {
        return fixed(BasisPoints.zero());
    }

    public boolean contains(BasisPoints rate) {
        return !rate.isGreaterThan(maxRate) && !minRate.isGreaterThan(rate);
    }

    public boolean isFixed() {
        return minRate.equals(maxRate);
    }
}