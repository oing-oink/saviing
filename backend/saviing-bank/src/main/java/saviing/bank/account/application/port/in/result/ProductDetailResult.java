package saviing.bank.account.application.port.in.result;

import java.util.Optional;

import lombok.NonNull;
import saviing.bank.account.domain.model.PaymentCycle;
import saviing.bank.account.domain.model.Product;
import saviing.bank.account.domain.model.TermUnit;
import saviing.bank.account.domain.vo.PaymentAmount;
import saviing.bank.account.domain.vo.ProductConfiguration;
import saviing.bank.account.domain.vo.TermConstraints;
import saviing.bank.account.domain.vo.TermPeriod;
import saviing.bank.common.vo.MoneyWon;

/**
 * 상품 상세 정보를 외부 계층으로 전달하기 위한 결과 객체입니다.
 */
public record ProductDetailResult(
    Long productId,
    String productName,
    String productCode,
    String productCategory,
    String description,
    String compoundingType,
    Short minInterestRateBps,
    Short maxInterestRateBps,
    String defaultPaymentCycle,
    Long minPaymentAmount,
    Long maxPaymentAmount,
    Long minimumBalance,
    TermConstraintsInfo termConstraints
) {

    /**
     * 도메인 상품 정보를 기반으로 결과 객체를 생성합니다.
     *
     * @param product 도메인 상품
     * @return 결과 객체
     */
    public static ProductDetailResult from(@NonNull Product product) {
        ProductConfiguration configuration = product.getConfiguration();
        Short minRate = null;
        Short maxRate = null;
        String paymentCycle = null;
        Long minAmount = null;
        Long maxAmount = null;
        Long minBalance = null;
        TermConstraintsInfo constraintsInfo = null;

        if (configuration != null) {
            if (configuration.getInterestRateRange() != null) {
                minRate = configuration.getInterestRateRange().minRate().value();
                maxRate = configuration.getInterestRateRange().maxRate().value();
            }

            paymentCycle = configuration.getDefaultPaymentCycle()
                .map(PaymentCycle::name)
                .orElse(null);

            Optional<PaymentAmount> paymentAmountRange = configuration.getPaymentAmountRange();
            if (paymentAmountRange.isPresent()) {
                PaymentAmount range = paymentAmountRange.get();
                minAmount = range.minAmount().amount();
                maxAmount = range.maxAmount().amount();
            }

            minBalance = configuration.getMinimumBalance()
                .map(MoneyWon::amount)
                .orElse(null);

            constraintsInfo = configuration.getTermConstraints()
                .map(ProductDetailResult::mapTermConstraints)
                .orElse(null);
        }

        return new ProductDetailResult(
            product.getId().value(),
            product.getName(),
            product.getCode(),
            product.getCategory().name(),
            product.getDescription(),
            configuration != null && configuration.getCompoundingType() != null ?
                configuration.getCompoundingType().name() : null,
            minRate,
            maxRate,
            paymentCycle,
            minAmount,
            maxAmount,
            minBalance,
            constraintsInfo
        );
    }

    private static TermConstraintsInfo mapTermConstraints(TermConstraints constraints) {
        TermPeriod min = constraints.getMinTerm();
        TermPeriod max = constraints.getMaxTerm();
        TermUnit unit = constraints.getStepUnit();
        if (unit == null) {
            unit = min != null ? min.unit() : (max != null ? max.unit() : null);
        }

        return new TermConstraintsInfo(
            min != null ? min.value() : null,
            max != null ? max.value() : null,
            unit != null ? unit.name() : null,
            constraints.getStepValue()
        );
    }

    /**
     * 기간 제약 정보를 표현하는 값 객체입니다.
     *
     * @param minValue 최소 기간 값
     * @param maxValue 최대 기간 값
     * @param unit 기간 단위
     * @param stepValue 증가 단위 값
     */
    public record TermConstraintsInfo(
        Integer minValue,
        Integer maxValue,
        String unit,
        Integer stepValue
    ) {}
}
