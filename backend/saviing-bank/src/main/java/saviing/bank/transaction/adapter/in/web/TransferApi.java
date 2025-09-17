package saviing.bank.transaction.adapter.in.web;

import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import saviing.bank.transaction.adapter.in.web.dto.request.TransferRequest;
import saviing.bank.transaction.adapter.in.web.dto.response.TransferResponse;
import saviing.common.response.ApiResult;
import saviing.common.response.ErrorResult;

/**
 * 송금 REST API 계약을 정의하는 인바운드 포트.
 */
@Tag(name = "송금", description = "계좌 간 송금 API")
public interface TransferApi {

    @Operation(
        summary = "계좌 간 송금",
        description = "내부 계좌 간 혹은 외부로 송금을 처리합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(implementation = TransferRequest.class)
            )
        )
    )
    @ApiResponse(
        responseCode = "201",
        description = "송금 처리 성공",
        useReturnTypeSchema = true
    )
    @ApiResponse(
        responseCode = "409",
        description = "동일 멱등키 송금 진행 중 또는 완료",
        content = @Content(schema = @Schema(implementation = ErrorResult.class))
    )
    @ApiResponse(
        responseCode = "422",
        description = "잔액 부족 또는 계좌 상태 오류",
        content = @Content(schema = @Schema(implementation = ErrorResult.class))
    )
    /**
     * 송금 요청을 처리한다.
     *
     * @param request 송금 요청 본문
     * @return 송금 결과 응답
     */
    ApiResult<TransferResponse> transfer(@Valid @RequestBody TransferRequest request);
}
