package saviing.bank.transaction.application.port.in;

import saviing.bank.transaction.application.port.in.command.VoidTransactionCommand;
import saviing.bank.transaction.application.port.in.result.TransactionResult;

public interface VoidTransactionUseCase {

    TransactionResult voidTransaction(VoidTransactionCommand command);
}