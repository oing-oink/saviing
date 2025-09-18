package saviing.bank.transaction.domain.model.transfer;

/**
 * Ledger 엔트리 상태.
 */
public enum LedgerEntryStatus {
    REQUESTED,
    PENDING,
    POSTED,
    VOID,
    FAILED;

    /**
     * 더 이상 상태 전이가 필요 없는 최종 상태인지 여부를 반환한다.
     */
    public boolean isTerminal() {
        return this == POSTED || this == VOID || this == FAILED;
    }
}
