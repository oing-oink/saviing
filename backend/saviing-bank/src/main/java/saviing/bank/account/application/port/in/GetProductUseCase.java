package saviing.bank.account.application.port.in;

import saviing.bank.account.application.port.in.result.ProductDetailResult;

/**
 * 상품 상세 정보를 조회하는 유스케이스입니다.
 */
public interface GetProductUseCase {

    /**
     * 상품 코드를 이용해 상품 상세 정보를 조회합니다.
     *
     * @param productCode 조회할 상품 코드
     * @return 상품 상세 정보
     */
    ProductDetailResult getProductByCode(String productCode);
}
