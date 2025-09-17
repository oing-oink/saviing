package saviing.bank.transaction.adapter.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import saviing.bank.transaction.adapter.in.web.dto.request.CreateTransactionRequest;
import saviing.bank.transaction.adapter.in.web.dto.request.VoidTransactionRequest;
import saviing.bank.transaction.adapter.in.web.dto.response.TransactionResponse;
import saviing.common.response.ApiResult;
import saviing.common.response.ErrorResult;

@Tag(name = "거래 관리", description = "거래 생성, 조회, 무효화 API")
public interface TransactionApi {

    @Operation(
        summary = "거래 생성",
        description = "입금, 출금, 이체 등의 거래를 생성합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "거래 생성 요청 정보",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreateTransactionRequest.class),
                examples = {
                    @ExampleObject(
                        name = "입금 거래 생성",
                        description = "계좌로 입금하는 거래 생성 예시",
                        value = """
                            {
                              "accountNumber": "1234567890123456",
                              "transactionType": "DEPOSIT",
                              "direction": "CREDIT",
                              "amount": 100000,
                              "valueDate": "2024-01-15",
                              "idempotencyKey": "deposit-20240115-001",
                              "description": "급여 입금"
                            }
                        """
                    ),
                    @ExampleObject(
                        name = "출금 거래 생성",
                        description = "계좌에서 출금하는 거래 생성 예시",
                        value = """
                            {
                              "accountNumber": "1234567890123456",
                              "transactionType": "WITHDRAWAL",
                              "direction": "DEBIT",
                              "amount": 50000,
                              "valueDate": "2024-01-15",
                              "idempotencyKey": "withdraw-20240115-001",
                              "description": "ATM 출금"
                            }
                        """
                    )
                }
            )
        )
    )
    @ApiResponse(
        responseCode = "201",
        description = "거래 생성 성공",
        useReturnTypeSchema = true
    )
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (유효하지 않은 금액/가치일/유형/방향)",
        content = @Content(schema = @Schema(implementation = ErrorResult.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "계좌를 찾을 수 없음",
        content = @Content(schema = @Schema(implementation = ErrorResult.class))
    )
    @ApiResponse(
        responseCode = "409",
        description = "중복된 거래(멱등키)",
        content = @Content(schema = @Schema(implementation = ErrorResult.class))
    )
    ApiResult<TransactionResponse> createTransaction(
        @Valid @RequestBody CreateTransactionRequest request
    );

    @Operation(
        summary = "거래 무효화",
        description = "잘못 처리된 거래나 취소해야 할 거래를 무효화합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "거래 무효화 성공",
        useReturnTypeSchema = true
    )
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (이미 무효화/유효하지 않은 상태)",
        content = @Content(schema = @Schema(implementation = ErrorResult.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "거래를 찾을 수 없음",
        content = @Content(schema = @Schema(implementation = ErrorResult.class))
    )
    ApiResult<TransactionResponse> voidTransaction(
        @Parameter(description = "무효화할 거래 ID", example = "1") @PathVariable Long transactionId,
        @Valid @RequestBody VoidTransactionRequest request
    );

    @Operation(
        summary = "거래 조회",
        description = "거래 ID로 특정 거래의 상세 정보를 조회합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "거래 조회 성공",
        useReturnTypeSchema = true
    )
    @ApiResponse(
        responseCode = "404",
        description = "거래를 찾을 수 없음",
        content = @Content(schema = @Schema(implementation = ErrorResult.class))
    )
    ApiResult<TransactionResponse> getTransaction(
        @Parameter(description = "조회할 거래 ID", example = "1") @PathVariable Long transactionId
    );

    @Operation(
        summary = "계좌별 거래 내역 조회",
        description = "특정 계좌의 거래 내역을 페이지네이션으로 조회합니다.",
        parameters = {
            @Parameter(
                name = "page",
                description = "페이지 번호 (0부터 시작)",
                example = "0"
            ),
            @Parameter(
                name = "size",
                description = "페이지당 조회할 거래 수",
                example = "20"
            )
        }
    )
    @ApiResponse(
        responseCode = "200",
        description = "거래 내역 조회 성공",
        useReturnTypeSchema = true
    )
    @ApiResponse(
        responseCode = "404",
        description = "계좌를 찾을 수 없음",
        content = @Content(schema = @Schema(implementation = ErrorResult.class))
    )
    ApiResult<List<TransactionResponse>> getTransactionsByAccount(
        @Parameter(description = "조회할 계좌번호", example = "1234567890123456") @PathVariable String accountNumber,
        @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size
    );
}
