package saviing.bank.transaction.application.port.in;

import java.util.List;

import saviing.bank.transaction.application.port.in.result.TransactionResult;

public interface GetTransactionsByAccountUseCase {

    List<TransactionResult> getTransactionsByAccount(Long accountId);

    List<TransactionResult> getTransactionsByAccount(Long accountId, int page, int size);

    List<TransactionResult> getTransactionsByAccount(String accountNumber);

    List<TransactionResult> getTransactionsByAccount(String accountNumber, int page, int size);
}