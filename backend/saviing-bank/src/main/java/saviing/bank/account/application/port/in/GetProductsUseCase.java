package saviing.bank.account.application.port.in;

import java.util.List;

import saviing.bank.account.application.port.in.result.ProductSummaryResult;

/**
 * 모든 상품에 대한 요약 정보를 조회하는 유스케이스입니다.
 */
public interface GetProductsUseCase {

    /**
     * 등록된 전체 금융 상품의 요약 정보를 조회합니다.
     *
     * @return 상품 요약 목록
     */
    List<ProductSummaryResult> getProducts();
}

