package saviing.bank.transaction.adapter.in.web;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import saviing.bank.transaction.adapter.in.web.dto.response.TransactionResponse;
import saviing.common.response.ApiResult;
import saviing.common.response.ErrorResult;

@Tag(name = "거래 관리", description = "송금 거래 조회 API")
public interface TransactionApi {

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
                description = "페이지당 조회할 거래 수 (최대 30)",
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
        @Parameter(description = "조회할 계좌 ID", example = "1") @PathVariable Long accountId,
        @Parameter(description = "페이지 번호 (0부터 시작)") @Min(0) @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기 (최대 30)") @Min(1) @Max(30) @RequestParam(defaultValue = "20") int size
    );
}
