package saviing.bank.transaction.application.port.out;

import saviing.bank.account.domain.model.Account;
import saviing.bank.common.vo.MoneyWon;

public interface UpdateAccountBalancePort {

    void updateBalance(Account account, MoneyWon newBalance);
}