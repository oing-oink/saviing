package saviing.bank.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import saviing.bank.account.domain.model.Product;
import saviing.bank.account.domain.model.ProductCategory;
import saviing.bank.account.domain.vo.ProductId;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Map<ProductId, Product> PRODUCTS = new HashMap<>();

    static {
        PRODUCTS.put(ProductId.of(1L), Product.of(
            ProductId.of(1L),
            ProductCategory.DEMAND_DEPOSIT,
            "자유입출금통장",
            "FREE_CHECKING"
        ));

        PRODUCTS.put(ProductId.of(2L), Product.of(
            ProductId.of(2L),
            ProductCategory.INSTALLMENT_SAVINGS,
            "자유적금",
            "FREE_SAVINGS"
        ));
    }

    public Product getProduct(ProductId productId) {
        return Optional.ofNullable(PRODUCTS.get(productId))
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다: " + productId));
    }
}