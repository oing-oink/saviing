package saviing.bank.account.adapter.in.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import saviing.bank.account.api.AccountInternalApi;
import saviing.bank.account.api.request.DepositAccountRequest;
import saviing.bank.account.api.request.WithdrawAccountRequest;
import saviing.bank.account.api.request.GetAccountRequest;
import saviing.bank.account.api.response.AccountApiResponse;
import saviing.bank.account.api.response.AccountInfoResponse;
import saviing.bank.account.api.response.BalanceUpdateResponse;
import saviing.bank.account.application.service.AccountBalanceService;
import saviing.bank.account.application.port.in.GetAccountUseCase;
import saviing.bank.account.application.port.in.result.BalanceUpdateResult;
import saviing.bank.account.application.port.in.result.GetAccountResult;
import saviing.common.annotation.ExecutionTime;

/**
 * Account 내부 API 컨트롤러
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ExecutionTime
public class AccountInternalApiImpl implements AccountInternalApi {

    private final AccountBalanceService accountBalanceService;
    private final GetAccountUseCase getAccountUseCase;

    @Override
    @Transactional
    public AccountApiResponse<BalanceUpdateResponse> withdraw(WithdrawAccountRequest request) {
        log.info("Processing withdraw request: accountId={}, amount={}",
            request.accountId(), request.amount());

        try {
            BalanceUpdateResult updateResult = accountBalanceService.withdraw(
                request.accountId(),
                request.amount()
            );

            BalanceUpdateResponse apiResult = BalanceUpdateResponse.of(
                updateResult.accountId(),
                updateResult.previousBalance(),
                updateResult.currentBalance(),
                updateResult.transactionAmount()
            );

            return AccountApiResponse.Success.of(apiResult);

        } catch (Exception e) {
            log.error("Error during withdraw: accountId=" + request.accountId(), e);
            return AccountApiResponse.Failure.of(e.getMessage());
        }
    }

    @Override
    @Transactional
    public AccountApiResponse<BalanceUpdateResponse> deposit(DepositAccountRequest request) {
        log.info("Processing deposit request: accountId={}, amount={}",
            request.accountId(), request.amount());

        try {
            BalanceUpdateResult updateResult = accountBalanceService.deposit(
                request.accountId(),
                request.amount()
            );

            BalanceUpdateResponse apiResult = BalanceUpdateResponse.of(
                updateResult.accountId(),
                updateResult.previousBalance(),
                updateResult.currentBalance(),
                updateResult.transactionAmount()
            );

            return AccountApiResponse.Success.of(apiResult);

        } catch (Exception e) {
            log.error("Error during deposit: accountId=" + request.accountId(), e);
            return AccountApiResponse.Failure.of(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccountApiResponse<AccountInfoResponse> getAccount(GetAccountRequest request) {
        log.debug("Processing getAccount request: accountId={}",
            request.accountId());

        try {
            GetAccountResult accountResult = getAccountUseCase.getAccount(request.accountId());
            AccountInfoResponse accountInfo = new AccountInfoResponse(
                accountResult.accountId(),
                accountResult.accountNumber(),
                accountResult.customerId(),
                accountResult.balance(),
                accountResult.status(),
                accountResult.product().productId()
            );

            return AccountApiResponse.Success.of(accountInfo);

        } catch (Exception e) {
            log.error("Error during getAccount: accountId=" + request.accountId(), e);
            return AccountApiResponse.Failure.of(e.getMessage());
        }
    }
}