package saviing.bank.transaction.domain.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.AccountStatus;
import saviing.bank.transaction.domain.model.TransactionType;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.exception.InsufficientBalanceException;
import saviing.bank.transaction.exception.InvalidAccountStateException;
import saviing.bank.transaction.exception.InvalidTransactionAmountException;
import saviing.bank.transaction.exception.InvalidValueDateException;

import java.util.Map;

@Service
public class TransactionValidationServiceImpl implements TransactionValidationService {

    @Override
    public void validateDebitTransaction(Account account, MoneyWon amount) {
        validateAccountStatus(account);

        if (account.getBalance().isLessThan(amount)) {
            throw new InsufficientBalanceException(
                Map.of(
                    "requestAmount", amount.amount(),
                    "currentBalance", account.getBalance().amount(),
                    "accountNumber", account.getAccountNumber().value()
                )
            );
        }
    }

    @Override
    public void validateAccountStatus(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStateException(
                Map.of(
                    "accountNumber", account.getAccountNumber().value(),
                    "currentStatus", account.getStatus().name()
                )
            );
        }
    }

    @Override
    public void validateTransactionAmount(MoneyWon amount, TransactionType transactionType) {
        if (amount == null || amount.amount() <= 0) {
            throw new InvalidTransactionAmountException(
                Map.of("amount", amount != null ? amount.amount() : "null")
            );
        }

        if (transactionType == TransactionType.FEE && amount.amount() > 100000) {
            throw new InvalidTransactionAmountException(
                Map.of(
                    "amount", amount.amount(),
                    "transactionType", transactionType.name(),
                    "maxFeeAmount", 100000
                )
            );
        }
    }

    @Override
    public void validateValueDate(LocalDate valueDate) {
        LocalDate today = LocalDate.now();
        if (valueDate.isAfter(today.plusDays(1))) {
            throw new InvalidValueDateException(
                Map.of(
                    "valueDate", valueDate.toString(),
                    "maxAllowedDate", today.plusDays(1).toString()
                )
            );
        }
        if (valueDate.isBefore(today.minusDays(30))) {
            throw new InvalidValueDateException(
                Map.of(
                    "valueDate", valueDate.toString(),
                    "minAllowedDate", today.minusDays(30).toString()
                )
            );
        }
    }
}