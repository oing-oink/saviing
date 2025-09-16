package saviing.bank.transaction.application.port.in;

import saviing.bank.transaction.application.port.in.command.CreateTransactionCommand;
import saviing.bank.transaction.application.port.in.command.CreateTransactionWithAccountNumberCommand;
import saviing.bank.transaction.application.port.in.result.TransactionResult;

public interface CreateTransactionUseCase {

    TransactionResult createTransaction(CreateTransactionCommand command);

    TransactionResult createTransaction(CreateTransactionWithAccountNumberCommand command);
}