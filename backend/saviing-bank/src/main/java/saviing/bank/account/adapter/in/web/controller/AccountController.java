package saviing.bank.account.adapter.in.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import saviing.bank.account.adapter.in.web.AccountApi;
import saviing.bank.account.adapter.in.web.dto.request.CreateAccountRequest;
import saviing.bank.account.adapter.in.web.dto.response.CreateAccountResponse;
import saviing.bank.account.application.port.in.CreateAccountUseCase;
import saviing.bank.account.application.port.in.result.CreateAccountResult;
import saviing.common.response.ApiResult;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController implements AccountApi {
    
    private final CreateAccountUseCase createAccountUseCase;
    
    @PostMapping
    public ApiResult<CreateAccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        CreateAccountResult result = createAccountUseCase.createAccount(request.toCommand());
        CreateAccountResponse response = CreateAccountResponse.from(result);
        
        return ApiResult.of(HttpStatus.CREATED, response);
    }
}