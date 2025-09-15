package saviing.bank.account.adapter.in.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import saviing.bank.account.adapter.in.web.AccountApi;
import saviing.bank.account.adapter.in.web.dto.request.CreateAccountRequest;
import saviing.bank.account.adapter.in.web.dto.response.CreateAccountResponse;
import saviing.bank.account.adapter.in.web.dto.response.GetAccountResponse;
import saviing.bank.account.application.port.in.CreateAccountUseCase;
import saviing.bank.account.application.port.in.GetAccountsByCustomerIdUseCase;
import saviing.bank.account.application.port.in.result.CreateAccountResult;
import saviing.bank.account.application.port.in.result.GetAccountResult;
import saviing.common.response.ApiResult;
import saviing.common.annotation.ExecutionTime;

@ExecutionTime
@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController implements AccountApi {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountsByCustomerIdUseCase getAccountsByCustomerIdUseCase;
    
    @PostMapping
    public ApiResult<CreateAccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        CreateAccountResult result = createAccountUseCase.createAccount(request.toCommand());
        CreateAccountResponse response = CreateAccountResponse.from(result);
        
        return ApiResult.of(HttpStatus.CREATED, response);
    }

    @GetMapping
    public ApiResult<List<GetAccountResponse>> getAccountsByCustomerId(@RequestParam Long customerId) {
        List<GetAccountResult> results = getAccountsByCustomerIdUseCase.getAccountsByCustomerId(customerId);
        List<GetAccountResponse> responses = results.stream()
            .map(GetAccountResponse::from)
            .toList();

        return ApiResult.of(HttpStatus.OK, responses);
    }
}