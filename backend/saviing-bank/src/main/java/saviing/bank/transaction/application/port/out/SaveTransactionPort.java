package saviing.bank.transaction.application.port.out;

import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.vo.TransactionId;

public interface SaveTransactionPort {

    TransactionId saveTransaction(Transaction transaction);

    void updateTransaction(Transaction transaction);
}