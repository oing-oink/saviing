package saviing.bank.account.application.port.out;

import java.util.List;
import java.util.Optional;

import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.AccountNumber;

public interface LoadAccountPort {
    
    Optional<Account> findById(AccountId id);
    
    Optional<Account> findByAccountNumber(AccountNumber accountNumber);
    
    List<Account> findByCustomerId(Long customerId);
    
    boolean existsByAccountNumber(AccountNumber accountNumber);
}