package saviing.bank.transaction.application.port.in;

import saviing.bank.transaction.application.port.in.result.TransactionResult;
import saviing.bank.transaction.domain.vo.TransactionId;

public interface GetTransactionUseCase {

    TransactionResult getTransaction(TransactionId transactionId);
}