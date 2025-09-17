package saviing.bank.transaction.domain.model;

/**
 * 송금 진행 상태. Ledger pair와 Transaction 상태 전이를 추적한다.
 */
public enum TransferStatus {
    REQUESTED,
    DEBIT_PENDING,
    DEBIT_POSTED,
    CREDIT_PENDING,
    CREDIT_POSTED,
    SETTLED,
    FAILED,
    VOID;

    /**
     * 더 이상 상태 전이가 없는 완료 상태인지 여부를 반환한다.
     */
    public boolean isTerminal() {
        return this == SETTLED || this == FAILED || this == VOID;
    }
}
