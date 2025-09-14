package saviing.bank.account.application.port.out;

import saviing.bank.account.domain.vo.AccountNumber;

public interface GenerateAccountNumberPort {
    
    AccountNumber generateUniqueAccountNumber();
}