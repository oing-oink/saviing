package saviing.bank.account.adapter.in.web.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import saviing.bank.account.application.port.in.GetProductUseCase;
import saviing.bank.account.application.port.in.GetProductsUseCase;
import saviing.bank.account.application.port.in.result.ProductDetailResult;
import saviing.bank.account.application.port.in.result.ProductSummaryResult;
import saviing.bank.account.adapter.in.web.ProductApi;
import saviing.bank.account.adapter.in.web.dto.response.ProductDetailResponse;
import saviing.bank.account.adapter.in.web.dto.response.ProductSummaryResponse;
import saviing.common.annotation.ExecutionTime;
import saviing.common.response.ApiResult;

@ExecutionTime
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController implements ProductApi {

    private final GetProductsUseCase getProductsUseCase;
    private final GetProductUseCase getProductUseCase;

    @Override
    @GetMapping
    public ApiResult<List<ProductSummaryResponse>> getProducts() {
        List<ProductSummaryResult> results = getProductsUseCase.getProducts();
        List<ProductSummaryResponse> responses = results.stream()
            .map(ProductSummaryResponse::from)
            .toList();

        return ApiResult.of(HttpStatus.OK, responses);
    }

    @Override
    @GetMapping("/{productCode:[A-Z][A-Z0-9_\\-]*}")
    public ApiResult<ProductDetailResponse> getProductByCode(@PathVariable String productCode) {
        ProductDetailResult result = getProductUseCase.getProductByCode(productCode);
        ProductDetailResponse response = ProductDetailResponse.from(result);

        return ApiResult.of(HttpStatus.OK, response);
    }
}
