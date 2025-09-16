package saviing.bank.account.domain.vo;

import org.springframework.lang.NonNull;
import saviing.bank.common.vo.MoneyWon;

public record PaymentAmount(
    @NonNull MoneyWon minAmount, // 최소 납입액
    @NonNull MoneyWon maxAmount // 최대 납입액
) {

    public PaymentAmount {
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