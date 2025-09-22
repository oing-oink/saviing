package saviing.bank.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import saviing.common.annotation.ExecutionTime;
import saviing.bank.account.application.port.in.GetAccountsByCustomerIdUseCase;
import saviing.bank.account.application.port.in.GetAccountUseCase;
import saviing.bank.account.application.port.in.result.GetAccountResult;
import saviing.bank.account.application.port.out.LoadAccountPort;
import saviing.bank.account.application.port.out.AutoTransferSchedulePort;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.Product;
import saviing.bank.account.domain.model.AutoTransferSchedule;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.AccountNumber;
import saviing.bank.account.exception.AccountNotFoundException;

@ExecutionTime
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountQueryService implements GetAccountUseCase, GetAccountsByCustomerIdUseCase {

    private final LoadAccountPort loadAccountPort;
    private final ProductService productService;
    private final AutoTransferSchedulePort autoTransferSchedulePort;

    @Override
    public GetAccountResult getAccount(Long accountId) {
        Account account = loadAccountPort.findById(AccountId.of(accountId))
            .orElseThrow(() -> new AccountNotFoundException(Map.of("accountId", accountId)));
        Product product = productService.getProduct(account.getProductId());
        AutoTransferSchedule schedule = account.isSavingsAccount()
            ? autoTransferSchedulePort.findByAccountId(account.getId()).orElse(null)
            : null;
        return GetAccountResult.from(account, product, schedule);
    }

    @Override
    public GetAccountResult getAccountByNumber(String accountNumber) {
        Account account = loadAccountPort.findByAccountNumber(new AccountNumber(accountNumber))
            .orElseThrow(() -> new AccountNotFoundException(Map.of("accountNumber", accountNumber)));
        Product product = productService.getProduct(account.getProductId());
        AutoTransferSchedule schedule = account.isSavingsAccount()
            ? autoTransferSchedulePort.findByAccountId(account.getId()).orElse(null)
            : null;
        return GetAccountResult.from(account, product, schedule);
    }

    @Override
    public List<GetAccountResult> getAccountsByCustomerId(Long customerId) {
        List<Account> accounts = loadAccountPort.findByCustomerId(customerId);

        return accounts.stream()
            .map(account -> {
                Product product = productService.getProduct(account.getProductId());
                AutoTransferSchedule schedule = account.isSavingsAccount()
                    ? autoTransferSchedulePort.findByAccountId(account.getId()).orElse(null)
                    : null;
                return GetAccountResult.from(account, product, schedule);
            })
            .toList();
    }
}
