package saviing.bank.account.domain.vo;

import java.util.Objects;

public record PaymentAmount(
    MoneyWon minAmount,
    MoneyWon maxAmount
) {

    public PaymentAmount {
        Objects.requireNonNull(minAmount, "최소 납입액은 필수입니다");
        Objects.requireNonNull(maxAmount, "최대 납입액은 필수입니다");

        if (minAmount.isGreaterThan(maxAmount)) {
            throw new IllegalArgumentException("최소 납입액은 최대 납입액보다 클 수 없습니다");
        }
    }

    public static PaymentAmount of(MoneyWon minAmount, MoneyWon maxAmount) {
        return new PaymentAmount(minAmount, maxAmount);
    }

    public static PaymentAmount of(long minAmount, long maxAmount) {
        return new PaymentAmount(MoneyWon.of(minAmount), MoneyWon.of(maxAmount));
    }

    public boolean contains(MoneyWon amount) {
        return !amount.isGreaterThan(maxAmount) && !minAmount.isGreaterThan(amount);
    }

    public boolean isValidAmount(MoneyWon amount) {
        return contains(amount);
    }
}