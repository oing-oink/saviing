package saviing.bank.transaction.application.port.out;

import java.util.Optional;

import saviing.bank.account.domain.model.Account;

public interface LoadAccountPort {

    Optional<Account> loadAccount(Long accountId);
}