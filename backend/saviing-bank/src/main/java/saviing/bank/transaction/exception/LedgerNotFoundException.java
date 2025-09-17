package saviing.bank.transaction.exception;

import java.util.Map;

/**
 * 요청한 TransferId에 해당하는 LedgerPair가 없을 때 발생하는 예외.
 */
public class LedgerNotFoundException extends TransactionException {

    public LedgerNotFoundException(Map<String, Object> context) {
        super(TransactionErrorType.LEDGER_NOT_FOUND, context);
    }
}
