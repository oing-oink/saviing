package saviing.bank.transaction.adapter.out.account;

import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import saviing.bank.account.application.port.out.LoadAccountPort;
import saviing.bank.account.application.port.out.SaveAccountPort;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.common.vo.MoneyWon;
import saviing.common.annotation.ExecutionTime;

@ExecutionTime
@Component
@RequiredArgsConstructor
public class AccountAdapter implements
    saviing.bank.transaction.application.port.out.LoadAccountPort,
    saviing.bank.transaction.application.port.out.UpdateAccountBalancePort {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;

    @Override
    public Optional<Account> loadAccount(Long accountId) {
        return loadAccountPort.findById(AccountId.of(accountId));
    }

    @Override
    public void updateBalance(Account account, MoneyWon newBalance) {
        MoneyWon currentBalance = account.getBalance();

        if (newBalance.isGreaterThan(currentBalance)) {
            MoneyWon depositAmount = newBalance.subtract(currentBalance);
            account.deposit(depositAmount);
        } else if (newBalance.isLessThan(currentBalance)) {
            MoneyWon withdrawAmount = currentBalance.subtract(newBalance);
            account.withdraw(withdrawAmount);
        }

        saveAccountPort.save(account);
    }
}