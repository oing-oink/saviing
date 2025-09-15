package saviing.bank.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import saviing.common.annotation.ExecutionTime;
import saviing.bank.account.domain.model.Product;
import saviing.bank.account.domain.model.ProductCategory;
import saviing.bank.account.domain.model.TermUnit;
import saviing.bank.account.domain.model.PaymentCycle;
import saviing.bank.account.domain.vo.ProductId;
import saviing.bank.account.domain.vo.TermConstraints;
import saviing.bank.account.domain.vo.TermPeriod;
import saviing.bank.account.domain.vo.ProductConfiguration;
import saviing.bank.account.domain.vo.InterestRateRange;
import saviing.bank.account.domain.vo.PaymentAmount;
import saviing.bank.account.domain.vo.BasisPoints;
import saviing.bank.account.domain.vo.MoneyWon;

@ExecutionTime
@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Map<ProductId, Product> PRODUCTS = new HashMap<>();

    static {
        // 자유입출금통장 - 0% 고정금리, 최소잔액 없음
        ProductConfiguration demandDepositConfig = ProductConfiguration.builder()
            .category(ProductCategory.DEMAND_DEPOSIT)
            .interestRateRange(InterestRateRange.zero())
            .minimumBalance(MoneyWon.zero())
            .build();

        PRODUCTS.put(ProductId.of(1L), Product.of(
            ProductId.of(1L),
            ProductCategory.DEMAND_DEPOSIT,
            "자유입출금통장",
            "FREE_CHECKING",
            demandDepositConfig
        ));

        // 자유적금 - 기본금리 2.5%, 보너스금리 0%~2%, 월납 기본, 최소 1만원~최대 100만원
        // 기간: 1주~15주, 1주 단위
        ProductConfiguration savingsConfig = ProductConfiguration.builder()
            .category(ProductCategory.INSTALLMENT_SAVINGS)
            .interestRateRange(InterestRateRange.of(BasisPoints.of(250), BasisPoints.of(450))) // 2.5%~4.5% (기본2.5% + 보너스0~2%)
            .defaultPaymentCycle(PaymentCycle.MONTHLY)
            .paymentAmount(PaymentAmount.of(10_000L, 1_000_000L))
            .termConstraints(TermConstraints.range(
                TermPeriod.weeks(1),    // 최소 1주
                TermPeriod.weeks(15),   // 최대 15주
                TermUnit.WEEKS,               // 주 단위로
                1                   // 1주씩 증가
            ))
            .build();

        PRODUCTS.put(ProductId.of(2L), Product.of(
            ProductId.of(2L),
            ProductCategory.INSTALLMENT_SAVINGS,
            "자유적금",
            "FREE_SAVINGS",
            savingsConfig
        ));
    }

    public Product getProduct(ProductId productId) {
        return Optional.ofNullable(PRODUCTS.get(productId))
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다: " + productId));
    }
}