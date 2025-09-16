package saviing.bank.transaction.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import saviing.bank.account.application.port.in.GetAccountUseCase;
import saviing.bank.account.application.port.in.result.GetAccountResult;
import saviing.bank.transaction.application.port.in.GetTransactionUseCase;
import saviing.bank.transaction.application.port.in.GetTransactionsByAccountUseCase;
import saviing.bank.transaction.application.port.in.result.TransactionResult;
import saviing.bank.transaction.exception.TransactionNotFoundException;

import java.util.Map;
import saviing.bank.transaction.application.port.out.LoadTransactionPort;
import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.vo.TransactionId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionQueryService implements GetTransactionUseCase, GetTransactionsByAccountUseCase {

    private final LoadTransactionPort loadTransactionPort;
    private final GetAccountUseCase getAccountUseCase;

    @Override
    public TransactionResult getTransaction(TransactionId transactionId) {
        Transaction transaction = loadTransactionPort.loadTransaction(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException(
                Map.of("transactionId", transactionId.value())
            ));

        return mapToResult(transaction);
    }

    @Override
    public List<TransactionResult> getTransactionsByAccount(Long accountId) {
        List<Transaction> transactions = loadTransactionPort.loadTransactionsByAccount(accountId);
        return transactions.stream()
            .map(this::mapToResult)
            .toList();
    }

    @Override
    public List<TransactionResult> getTransactionsByAccount(Long accountId, int page, int size) {
        List<Transaction> transactions = loadTransactionPort.loadTransactionsByAccount(accountId, page, size);
        return transactions.stream()
            .map(this::mapToResult)
            .toList();
    }

    @Override
    public List<TransactionResult> getTransactionsByAccount(String accountNumber) {
        Long accountId = getAccountIdByNumber(accountNumber);
        return getTransactionsByAccount(accountId);
    }

    @Override
    public List<TransactionResult> getTransactionsByAccount(String accountNumber, int page, int size) {
        Long accountId = getAccountIdByNumber(accountNumber);
        return getTransactionsByAccount(accountId, page, size);
    }

    private Long getAccountIdByNumber(String accountNumber) {
        GetAccountResult account = getAccountUseCase.getAccountByNumber(accountNumber);
        return account.accountId();
    }

    private TransactionResult mapToResult(Transaction transaction) {
        return TransactionResult.from(transaction);
    }
}