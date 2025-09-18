package saviing.bank.account.application.port.in.result;

import lombok.NonNull;
import saviing.bank.account.domain.model.Product;
import saviing.bank.account.domain.vo.ProductConfiguration;

/**
 * 상품 목록 응답에 사용되는 요약 정보입니다.
 */
public record ProductSummaryResult(
    Long productId,
    String productName,
    String productCode,
    String productCategory,
    String description,
    Short minInterestRateBps,
    Short maxInterestRateBps
) {

    /**
     * 도메인 상품 객체를 요약 정보로 변환합니다.
     *
     * @param product 도메인 상품
     * @return 요약 정보
     */
    public static ProductSummaryResult from(@NonNull Product product) {
        ProductConfiguration configuration = product.getConfiguration();
        Short minRate = null;
        Short maxRate = null;
        if (configuration != null && configuration.getInterestRateRange() != null) {
            minRate = configuration.getInterestRateRange().minRate().value();
            maxRate = configuration.getInterestRateRange().maxRate().value();
        }

        return new ProductSummaryResult(
            product.getId().value(),
            product.getName(),
            product.getCode(),
            product.getCategory().name(),
            product.getDescription(),
            minRate,
            maxRate
        );
    }
}

