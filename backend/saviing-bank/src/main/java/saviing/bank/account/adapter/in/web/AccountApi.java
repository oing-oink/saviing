package saviing.bank.account.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import saviing.bank.account.adapter.in.web.dto.request.CreateAccountRequest;
import saviing.bank.account.adapter.in.web.dto.response.CreateAccountResponse;
import saviing.bank.account.adapter.in.web.dto.response.GetAccountResponse;
import saviing.common.response.ApiResult;
import saviing.common.response.ErrorResult;

@Tag(name = "계좌 관리", description = "계좌 생성, 조회, 관리 API")
public interface AccountApi {

    @Operation(
        summary = "계좌 생성",
        description = "자유입출금 계좌 또는 적금 계좌를 생성합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "계좌 생성 요청 정보",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreateAccountRequest.class),
                examples = {
                    @ExampleObject(
                        name = "자유입출금 계좌 생성",
                        description = "자유입출금 계좌 생성 예시",
                        value = """
                            {
                              "customerId": 1001,
                              "productId": 1
                            }
                        """
                    ),
                    @ExampleObject(
                        name = "자유적금 계좌 생성",
                        description = "자유적금 계좌 생성 예시",
                        value = """
                            {
                              "customerId": 1001,
                              "productId": 2,
                              "targetAmount": 1000000,
                              "termPeriod": {
                                "value": 12,
                                "unit": "WEEKS"
                              },
                              "maturityWithdrawalAccount": "11012345678901234"
                            }
                        """
                    )
                }
            )
        )
    )
    @ApiResponse(
        responseCode = "201",
        description = "계좌 생성 성공",
        useReturnTypeSchema = true
    )
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (유효하지 않은 금액, 상품 타입, 적금 기간 등)",
        content = @Content(schema = @Schema(implementation = ErrorResult.class))
    )
    @ApiResponse(
        responseCode = "409",
        description = "계좌 중복",
        content = @Content(schema = @Schema(implementation = ErrorResult.class))
    )
    ApiResult<CreateAccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request);

    @Operation(
        summary = "고객 계좌 목록 조회",
        description = "특정 고객의 모든 계좌를 상품 정보와 함께 조회합니다.",
        parameters = @Parameter(
            name = "customerId",
            description = "조회할 고객 ID",
            required = true,
            example = "1001"
        )
    )
    @ApiResponse(
        responseCode = "200",
        description = "계좌 목록 조회 성공",
        useReturnTypeSchema = true
    )
    ApiResult<List<GetAccountResponse>> getAccountsByCustomerId(@RequestParam Long customerId);

    @Operation(
        summary = "계좌 조회",
        description = "계좌번호로 계좌 상세 정보를 조회합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "계좌 조회 성공",
        useReturnTypeSchema = true
    )
    @ApiResponse(
        responseCode = "404",
        description = "계좌를 찾을 수 없음",
        content = @Content(schema = @Schema(implementation = ErrorResult.class))
    )
    ApiResult<GetAccountResponse> getAccount(
        @Parameter(description = "조회할 계좌번호", example = "11012345678901234")
        @PathVariable String accountNumber
    );

    @Operation(
        summary = "계좌 ID로 계좌 조회",
        description = "계좌 ID로 계좌 상세 정보를 조회합니다. (내부용)"
    )
    @ApiResponse(
        responseCode = "200",
        description = "계좌 조회 성공",
        useReturnTypeSchema = true
    )
    @ApiResponse(
        responseCode = "404",
        description = "계좌를 찾을 수 없음",
        content = @Content(schema = @Schema(implementation = ErrorResult.class))
    )
    ApiResult<GetAccountResponse> getAccountById(
        @Parameter(description = "조회할 계좌 ID", example = "1")
        @PathVariable Long accountId
    );
}
