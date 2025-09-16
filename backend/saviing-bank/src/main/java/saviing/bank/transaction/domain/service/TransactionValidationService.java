package saviing.bank.transaction.domain.service;

import java.time.LocalDate;

import saviing.bank.account.domain.model.Account;
import saviing.bank.transaction.domain.model.TransactionType;
import saviing.bank.common.vo.MoneyWon;

public interface TransactionValidationService {

    void validateDebitTransaction(Account account, MoneyWon amount);

    void validateAccountStatus(Account account);

    void validateTransactionAmount(MoneyWon amount, TransactionType transactionType);

    void validateValueDate(LocalDate valueDate);
}