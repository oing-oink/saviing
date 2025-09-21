package saviing.bank.account.adapter.in.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import saviing.bank.account.adapter.in.web.AccountApi;
import saviing.bank.account.adapter.in.web.dto.request.CreateAccountRequest;
import saviing.bank.account.adapter.in.web.dto.request.UpdateAccountStatusRequest;
import saviing.bank.account.adapter.in.web.dto.request.UpdateSavingsAccountRequest;
import saviing.bank.account.adapter.in.web.dto.response.CreateAccountResponse;
import saviing.bank.account.adapter.in.web.dto.response.GetAccountResponse;
import saviing.bank.account.application.port.in.CreateAccountUseCase;
import saviing.bank.account.application.port.in.CloseSavingsAccountUseCase;
import saviing.bank.account.application.port.in.GetAccountsByCustomerIdUseCase;
import saviing.bank.account.application.port.in.GetAccountUseCase;
import saviing.bank.account.application.port.in.UpdateSavingsAccountUseCase;
import saviing.bank.account.application.port.in.UpdateAutoTransferScheduleUseCase;
import saviing.bank.account.application.port.in.result.CreateAccountResult;
import saviing.bank.account.application.port.in.result.GetAccountResult;
import saviing.bank.account.application.port.in.command.CloseSavingsAccountCommand;
import saviing.bank.account.application.port.in.command.UpdateAutoTransferScheduleCommand;
import saviing.bank.account.exception.InvalidAccountStateException;
import saviing.common.response.ApiResult;
import saviing.common.annotation.ExecutionTime;
import saviing.bank.account.adapter.in.web.dto.request.UpdateAutoTransferRequest;

@ExecutionTime
@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController implements AccountApi {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountsByCustomerIdUseCase getAccountsByCustomerIdUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final UpdateSavingsAccountUseCase updateSavingsAccountUseCase;
    private final CloseSavingsAccountUseCase closeSavingsAccountUseCase;
    private final UpdateAutoTransferScheduleUseCase updateAutoTransferScheduleUseCase;
    
    @PostMapping
    public ApiResult<CreateAccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        CreateAccountResult result = createAccountUseCase.createAccount(request.toCommand());
        CreateAccountResponse response = CreateAccountResponse.from(result);
        
        return ApiResult.of(HttpStatus.CREATED, response);
    }

    @PatchMapping("/id/{accountId}/savings/auto-transfer")
    public ApiResult<GetAccountResponse> updateAutoTransfer(
        @PathVariable Long accountId,
        @Valid @RequestBody UpdateAutoTransferRequest request
    ) {
        UpdateAutoTransferScheduleCommand command = request.toCommand(accountId);
        GetAccountResult result = updateAutoTransferScheduleUseCase.updateAutoTransferSchedule(command);
        GetAccountResponse response = GetAccountResponse.from(result);

        return ApiResult.of(HttpStatus.OK, response);
    }

    @GetMapping
    public ApiResult<List<GetAccountResponse>> getAccountsByCustomerId(@RequestParam Long customerId) {
        List<GetAccountResult> results = getAccountsByCustomerIdUseCase.getAccountsByCustomerId(customerId);
        List<GetAccountResponse> responses = results.stream()
            .map(GetAccountResponse::from)
            .toList();

        return ApiResult.of(HttpStatus.OK, responses);
    }

    @GetMapping("/{accountNumber:\\d{1,32}}")
    public ApiResult<GetAccountResponse> getAccount(@PathVariable String accountNumber) {
        GetAccountResult result = getAccountUseCase.getAccountByNumber(accountNumber);
        GetAccountResponse response = GetAccountResponse.from(result);

        return ApiResult.of(HttpStatus.OK, response);
    }

    @GetMapping("/id/{accountId}")
    public ApiResult<GetAccountResponse> getAccountById(@PathVariable Long accountId) {
        GetAccountResult result = getAccountUseCase.getAccount(accountId);
        GetAccountResponse response = GetAccountResponse.from(result);

        return ApiResult.of(HttpStatus.OK, response);
    }

    @PatchMapping("/id/{accountId}")
    public ApiResult<GetAccountResponse> updateSavingsAccount(
        @PathVariable Long accountId,
        @Valid @RequestBody UpdateSavingsAccountRequest request
    ) {
        GetAccountResult result = updateSavingsAccountUseCase.updateSavingsAccount(request.toCommand(accountId));
        GetAccountResponse response = GetAccountResponse.from(result);

        return ApiResult.of(HttpStatus.OK, response);
    }

    @PatchMapping("/id/{accountId}/status")
    public ApiResult<GetAccountResponse> updateAccountStatus(
        @PathVariable Long accountId,
        @Valid @RequestBody UpdateAccountStatusRequest request
    ) {
        if (!"CLOSED".equalsIgnoreCase(request.status())) {
            throw new InvalidAccountStateException(Map.of(
                "accountId", accountId,
                "requestedStatus", request.status(),
                "reason", "STATUS_NOT_SUPPORTED"
            ));
        }

        GetAccountResult result = closeSavingsAccountUseCase.closeSavingsAccount(
            CloseSavingsAccountCommand.of(accountId)
        );
        GetAccountResponse response = GetAccountResponse.from(result);

        return ApiResult.of(HttpStatus.OK, response);
    }
}
