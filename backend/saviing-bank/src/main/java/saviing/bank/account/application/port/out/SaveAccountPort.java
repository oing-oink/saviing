package saviing.bank.account.application.port.out;

import saviing.bank.account.domain.model.Account;

public interface SaveAccountPort {
    
    Account save(Account account);
    
    void delete(Account account);
}