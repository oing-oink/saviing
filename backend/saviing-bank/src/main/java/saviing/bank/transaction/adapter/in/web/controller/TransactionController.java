package saviing.bank.transaction.adapter.in.web.controller;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import saviing.bank.transaction.adapter.in.web.TransactionApi;
import saviing.bank.transaction.adapter.in.web.dto.response.TransactionResponse;
import saviing.bank.transaction.application.port.in.GetTransactionUseCase;
import saviing.bank.transaction.application.port.in.GetTransactionsByAccountUseCase;
import saviing.bank.transaction.application.port.in.result.TransactionResult;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.common.annotation.ExecutionTime;
import saviing.common.response.ApiResult;

@ExecutionTime
@Validated
@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
public class TransactionController implements TransactionApi {

    private final GetTransactionUseCase getTransactionUseCase;
    private final GetTransactionsByAccountUseCase getTransactionsByAccountUseCase;

    @Override
    @GetMapping("/{transactionId}")
    public ApiResult<TransactionResponse> getTransaction(@PathVariable Long transactionId) {
        TransactionResult result = getTransactionUseCase.getTransaction(TransactionId.of(transactionId));
        TransactionResponse response = TransactionResponse.from(result);

        return ApiResult.of(HttpStatus.OK, response);
    }

    @Override
    @GetMapping("/accounts/{accountId}")
    public ApiResult<List<TransactionResponse>> getTransactionsByAccount(
        @PathVariable Long accountId,
        @Min(0) @RequestParam(defaultValue = "0") int page,
        @Min(1) @Max(30) @RequestParam(defaultValue = "20") int size
    ) {
        List<TransactionResult> results = getTransactionsByAccountUseCase
            .getTransactionsByAccount(accountId, page, size);

        List<TransactionResponse> responses = results.stream()
            .map(TransactionResponse::from)
            .toList();

        return ApiResult.of(HttpStatus.OK, responses);
    }

}
