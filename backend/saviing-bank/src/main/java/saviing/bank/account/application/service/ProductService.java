package saviing.bank.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

import saviing.bank.account.application.port.in.GetProductUseCase;
import saviing.bank.account.application.port.in.GetProductsUseCase;
import saviing.bank.account.application.port.in.result.ProductDetailResult;
import saviing.bank.account.application.port.in.result.ProductSummaryResult;
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
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.account.exception.ProductNotFoundException;

@ExecutionTime
@Service
@RequiredArgsConstructor
public class ProductService implements GetProductUseCase, GetProductsUseCase {

    private static final Map<ProductId, Product> PRODUCTS = new LinkedHashMap<>();
    private static final Map<String, Product> PRODUCTS_BY_CODE = new LinkedHashMap<>();

    static {
        // 자유입출금통장 - 0% 고정금리, 최소잔액 없음
        ProductConfiguration demandDepositConfig = ProductConfiguration.builder()
            .category(ProductCategory.DEMAND_DEPOSIT)
            .interestRateRange(InterestRateRange.zero())
            .minimumBalance(MoneyWon.zero())
            .build();

        Product demandDeposit = Product.of(
            ProductId.of(1L),
            ProductCategory.DEMAND_DEPOSIT,
            "자유입출금통장",
            "FREE_CHECKING",
            "언제든지 자유롭게 입출금이 가능한 상품",
            demandDepositConfig
        );
        registerProduct(demandDeposit);

        // 자유적금 - 기본금리 1.5%, 보너스금리 0%~3%, 월납 기본, 최소 1만원~최대 1000만원
        // 기간: 4주~24주, 1주 단위
        ProductConfiguration savingsConfig = ProductConfiguration.builder()
            .category(ProductCategory.INSTALLMENT_SAVINGS)
            .interestRateRange(InterestRateRange.of(BasisPoints.of(150), BasisPoints.of(450))) // 1.5%~4.5% (기본1.5% + 보너스0~3%)
            .defaultPaymentCycle(PaymentCycle.MONTHLY)
            .paymentAmount(PaymentAmount.of(10_000L, 10_000_000L))
            .termConstraints(TermConstraints.range(
                TermPeriod.weeks(4), // 최소 4주
                TermPeriod.weeks(24), // 최대 24주
                TermUnit.WEEKS, // 주 단위로
                1 // 1주씩 증가
            ))
            .build();

        Product savings = Product.of(
            ProductId.of(2L),
            ProductCategory.INSTALLMENT_SAVINGS,
            "자유적금",
            "FREE_SAVINGS",
            "자유롭게 적금하고, 펫을 키우며 금리를 높여가는 금융 상품",
            savingsConfig
        );
        registerProduct(savings);
    }

    public Product getProduct(ProductId productId) {
        return Optional.ofNullable(PRODUCTS.get(productId))
            .orElseThrow(() -> new ProductNotFoundException(productId.value()));
    }

    private static void registerProduct(Product product) {
        PRODUCTS.put(product.getId(), product);
        PRODUCTS_BY_CODE.put(product.getCode(), product);
    }

    /**
     * 상품 코드를 기반으로 상품을 조회합니다.
     *
     * @param productCode 조회할 상품 코드
     * @return 상품 정보
     * @throws ProductNotFoundException 등록되지 않은 상품 코드일 때
     */
    public Product findProductByCode(String productCode) {
        return Optional.ofNullable(PRODUCTS_BY_CODE.get(productCode))
            .orElseThrow(() -> new ProductNotFoundException(productCode));
    }

    @Override
    public ProductDetailResult getProductByCode(String productCode) {
        Product product = findProductByCode(productCode);
        return ProductDetailResult.from(product);
    }

    @Override
    public List<ProductSummaryResult> getProducts() {
        return PRODUCTS.values().stream()
            .map(ProductSummaryResult::from)
            .collect(Collectors.toList());
    }
}
