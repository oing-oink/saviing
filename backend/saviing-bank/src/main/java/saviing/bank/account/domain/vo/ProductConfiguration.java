package saviing.bank.account.domain.vo;

import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

import saviing.bank.account.domain.model.CompoundingType;
import saviing.bank.account.domain.model.PaymentCycle;
import saviing.bank.account.domain.model.ProductCategory;
import saviing.bank.common.vo.MoneyWon;

@Getter
@Builder
public class ProductConfiguration {

    private final ProductCategory category;
    @Builder.Default
    private final CompoundingType compoundingType = CompoundingType.DAILY;
    @Builder.Default
    private final InterestRateRange interestRateRange = InterestRateRange.zero();
    private final PaymentCycle defaultPaymentCycle;   // 적금 전용
    private final PaymentAmount paymentAmount;        // 적금 전용
    private final MoneyWon minimumBalance;            // 요구불예금 전용
    private final TermConstraints termConstraints;    // 기간 제약 조건 (적금/정기예금 전용)

    // 타입 안전한 getter 메서드들
    public Optional<PaymentAmount> getPaymentAmountRange() {
        return Optional.ofNullable(paymentAmount);
    }

    public Optional<PaymentCycle> getDefaultPaymentCycle() {
        return Optional.ofNullable(defaultPaymentCycle);
    }

    public Optional<MoneyWon> getMinimumBalance() {
        return Optional.ofNullable(minimumBalance);
    }

    public Optional<TermConstraints> getTermConstraints() {
        return Optional.ofNullable(termConstraints);
    }

    // 검증 메서드들
    public boolean isValidPaymentAmount(MoneyWon amount) {
        return paymentAmount != null && paymentAmount.contains(amount);
    }

    public boolean isValidInterestRate(BasisPoints rate) {
        return interestRateRange.contains(rate);
    }

    public boolean isValidTerm(TermPeriod term) {
        return termConstraints != null && termConstraints.isValidTerm(term);
    }
}