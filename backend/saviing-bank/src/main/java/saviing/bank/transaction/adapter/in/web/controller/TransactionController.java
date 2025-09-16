package saviing.bank.transaction.adapter.in.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import saviing.bank.transaction.adapter.in.web.TransactionApi;
import saviing.bank.transaction.adapter.in.web.dto.request.CreateTransactionRequest;
import saviing.bank.transaction.adapter.in.web.dto.request.VoidTransactionRequest;
import saviing.bank.transaction.adapter.in.web.dto.response.TransactionResponse;
import saviing.bank.transaction.application.port.in.CreateTransactionUseCase;
import saviing.bank.transaction.application.port.in.GetTransactionUseCase;
import saviing.bank.transaction.application.port.in.GetTransactionsByAccountUseCase;
import saviing.bank.transaction.application.port.in.VoidTransactionUseCase;
import saviing.bank.transaction.application.port.in.command.CreateTransactionCommand;
import saviing.bank.transaction.application.port.in.command.CreateTransactionWithAccountNumberCommand;
import saviing.bank.transaction.application.port.in.command.VoidTransactionCommand;
import saviing.bank.transaction.application.port.in.result.TransactionResult;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.common.annotation.ExecutionTime;
import saviing.common.response.ApiResult;

@ExecutionTime
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController implements TransactionApi {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final VoidTransactionUseCase voidTransactionUseCase;
    private final GetTransactionUseCase getTransactionUseCase;
    private final GetTransactionsByAccountUseCase getTransactionsByAccountUseCase;

    @Override
    @PostMapping
    public ApiResult<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        var command = request.toCommand();

        TransactionResult result = createTransactionUseCase.createTransaction(command);
        TransactionResponse response = TransactionResponse.from(result);

        return ApiResult.of(HttpStatus.CREATED, response);
    }

    @Override
    @PutMapping("/{transactionId}/void")
    public ApiResult<TransactionResponse> voidTransaction(
        @PathVariable Long transactionId,
        @Valid @RequestBody VoidTransactionRequest request
    ) {
        VoidTransactionCommand command = request.toCommand(transactionId);

        TransactionResult result = voidTransactionUseCase.voidTransaction(command);
        TransactionResponse response = TransactionResponse.from(result);

        return ApiResult.of(HttpStatus.OK, response);
    }

    @Override
    @GetMapping("/{transactionId}")
    public ApiResult<TransactionResponse> getTransaction(@PathVariable Long transactionId) {
        TransactionResult result = getTransactionUseCase.getTransaction(TransactionId.of(transactionId));
        TransactionResponse response = TransactionResponse.from(result);

        return ApiResult.of(HttpStatus.OK, response);
    }

    @Override
    @GetMapping("/accounts/{accountNumber}")
    public ApiResult<List<TransactionResponse>> getTransactionsByAccount(
        @PathVariable String accountNumber,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        List<TransactionResult> results = getTransactionsByAccountUseCase
            .getTransactionsByAccount(accountNumber, page, size);

        List<TransactionResponse> responses = results.stream()
            .map(TransactionResponse::from)
            .toList();

        return ApiResult.of(HttpStatus.OK, responses);
    }

    @GetMapping("/accounts/id/{accountId}")
    public ApiResult<List<TransactionResponse>> getTransactionsByAccountId(
        @PathVariable Long accountId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        List<TransactionResult> results = getTransactionsByAccountUseCase
            .getTransactionsByAccount(accountId, page, size);

        List<TransactionResponse> responses = results.stream()
            .map(TransactionResponse::from)
            .toList();

        return ApiResult.of(HttpStatus.OK, responses);
    }

}