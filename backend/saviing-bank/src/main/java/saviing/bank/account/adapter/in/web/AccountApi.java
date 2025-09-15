package saviing.bank.account.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

import saviing.bank.account.adapter.in.web.dto.request.CreateAccountRequest;
import saviing.bank.account.adapter.in.web.dto.response.CreateAccountResponse;
import saviing.common.response.ApiResult;

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
    ApiResult<CreateAccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request);
}