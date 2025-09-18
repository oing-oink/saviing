package saviing.bank.account.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import saviing.bank.account.adapter.in.web.dto.response.ProductDetailResponse;
import saviing.bank.account.adapter.in.web.dto.response.ProductSummaryResponse;
import saviing.common.response.ApiResult;

@Tag(name = "상품 관리", description = "금융 상품 조회 API")
public interface ProductApi {

    @Operation(
        summary = "상품 목록 조회",
        description = "등록된 모든 금융 상품의 요약 정보를 조회합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "상품 목록 조회 성공",
        useReturnTypeSchema = true
    )
    ApiResult<List<ProductSummaryResponse>> getProducts();

    @Operation(
        summary = "상품 코드로 상품 조회",
        description = "상품 코드를 통해 금융 상품 상세 정보를 조회합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "상품 조회 성공",
        useReturnTypeSchema = true
    )
    @ApiResponse(
        responseCode = "404",
        description = "상품을 찾을 수 없음",
        content = @Content(
            schema = @Schema(implementation = saviing.common.response.ErrorResult.class)
        )
    )
    ApiResult<ProductDetailResponse> getProductByCode(
        @Parameter(description = "상품 코드", example = "FREE_SAVINGS")
        @PathVariable String productCode
    );
}
