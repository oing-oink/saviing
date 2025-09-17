package saviing.bank.transaction.adapter.in.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import saviing.bank.transaction.adapter.in.web.TransferApi;
import saviing.bank.transaction.adapter.in.web.dto.request.TransferRequest;
import saviing.bank.transaction.adapter.in.web.dto.response.TransferResponse;
import saviing.bank.transaction.application.port.in.TransferUseCase;
import saviing.bank.transaction.application.port.in.command.TransferCommand;
import saviing.bank.transaction.application.port.in.result.TransferResult;
import saviing.common.annotation.ExecutionTime;
import saviing.common.response.ApiResult;

/**
 * 송금 REST 요청을 받아 애플리케이션 포트에 위임하는 컨트롤러.
 */
@ExecutionTime
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransferController implements TransferApi {

    private final TransferUseCase transferUseCase;

    /**
     * REST 엔드포인트에서 송금 요청을 처리한다.
     */
    @Override
    @PostMapping("/transfer")
    public ApiResult<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        TransferCommand command = request.toCommand();
        TransferResult result = transferUseCase.transfer(command);
        return ApiResult.of(HttpStatus.CREATED, TransferResponse.from(result));
    }
}
