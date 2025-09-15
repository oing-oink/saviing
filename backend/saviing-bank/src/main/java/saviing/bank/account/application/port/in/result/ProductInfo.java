package saviing.bank.account.application.port.in.result;

import lombok.NonNull;
import saviing.bank.account.domain.model.Product;

public record ProductInfo(
    Long productId,
    String productName,
    String productCode,
    String productCategory,
    String description
) {

    public static ProductInfo from(@NonNull Product product) {
        return new ProductInfo(
            product.getId().value(),
            product.getName(),
            product.getCode(),
            product.getCategory().name(),
            product.getDescription()
        );
    }
}