package saviing.bank.transaction.exception;

import java.math.BigDecimal;
import java.util.Map;

public class InvalidTransactionAmountException extends TransactionException {

    public InvalidTransactionAmountException(BigDecimal amount) {
        super(TransactionErrorType.INVALID_TRANSACTION_AMOUNT,
              Map.of("amount", amount));
    }

    public InvalidTransactionAmountException(String message, BigDecimal amount) {
        super(TransactionErrorType.INVALID_TRANSACTION_AMOUNT, message,
              Map.of("amount", amount));
    }

    public InvalidTransactionAmountException(Map<String, Object> context) {
        super(TransactionErrorType.INVALID_TRANSACTION_AMOUNT, context);
    }
}