package saviing.bank.account.exception;

/**
 * 요청한 상품을 찾을 수 없을 때 발생하는 예외입니다.
 */
public class ProductNotFoundException extends ProductException {

    public ProductNotFoundException(String productCode) {
        super(ProductErrorCode.PRODUCT_NOT_FOUND, "상품 코드를 찾을 수 없습니다: " + productCode);
    }

    public ProductNotFoundException(Long productId) {
        super(ProductErrorCode.PRODUCT_NOT_FOUND, "상품 ID를 찾을 수 없습니다: " + productId);
    }
}
