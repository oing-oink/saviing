package saviing.bank.account.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import saviing.bank.account.application.port.in.result.ProductInfo;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "상품 정보")
public record ProductDto(
    @Schema(description = "상품 ID", example = "1")
    Long productId,

    @Schema(description = "상품명", example = "자유입출금통장")
    String productName,

    @Schema(description = "상품 코드", example = "FREE_CHECKING")
    String productCode,

    @Schema(description = "상품 카테고리", example = "DEMAND_DEPOSIT")
    String productCategory,

    @Schema(description = "상품 설명", example = "언제든지 자유롭게 입출금이 가능한 통장")
    String description
) {

    public static ProductDto from(ProductInfo productInfo) {
        return ProductDto.builder()
            .productId(productInfo.productId())
            .productName(productInfo.productName())
            .productCode(productInfo.productCode())
            .productCategory(productInfo.productCategory())
            .description(productInfo.description())
            .build();
    }
}