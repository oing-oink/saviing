package saviing.bank.account.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import saviing.bank.account.application.port.in.result.ProductDetailResult;
import saviing.bank.account.application.port.in.result.ProductSummaryResult;
import saviing.bank.account.exception.ProductNotFoundException;

class ProductServiceTest {

    private final ProductService productService = new ProductService();

    @Test
    void getProducts_shouldReturnAllRegisteredProductsInRegistrationOrder() {
        List<ProductSummaryResult> products = productService.getProducts();

        assertThat(products)
            .hasSize(2)
            .extracting(ProductSummaryResult::productCode)
            .containsExactly("FREE_CHECKING", "FREE_SAVINGS");
    }

    @Test
    void getProductByCode_shouldReturnDetailedInformation() {
        ProductDetailResult result = productService.getProductByCode("FREE_SAVINGS");

        assertThat(result.productName()).isEqualTo("자유적금");
        assertThat(result.minInterestRateBps()).isEqualTo((short) 250);
        assertThat(result.maxInterestRateBps()).isEqualTo((short) 450);
        assertThat(result.termConstraints()).isNotNull();
    }

    @Test
    void getProductByCode_shouldThrowWhenProductDoesNotExist() {
        assertThatThrownBy(() -> productService.getProductByCode("UNKNOWN"))
            .isInstanceOf(ProductNotFoundException.class);
    }
}
