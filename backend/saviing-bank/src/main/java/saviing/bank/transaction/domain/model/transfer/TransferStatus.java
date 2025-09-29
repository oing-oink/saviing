package saviing.bank.transaction.domain.model.transfer;

/**
 * 송금 진행 상태를 나타내는 열거형
 * Ledger pair와 Transaction 상태 전이를 추적한다.
 */
public enum TransferStatus {
    /** 송금 요청 */
    REQUESTED,
    /** 출금 대기 중 */
    DEBIT_PENDING,
    /** 출금 완료 */
    DEBIT_POSTED,
    /** 입금 대기 중 */
    CREDIT_PENDING,
    /** 입금 완료 */
    CREDIT_POSTED,
    /** 송금 완료 */
    SETTLED,
    /** 송금 실패 */
    FAILED,
    /** 송금 취소 */
    VOID;

    /**
     * 더 이상 상태 전이가 없는 종료 상태인지 확인한다
     *
     * @return 종료 상태이면 true, 아니면 false
     */
    public boolean isTerminal() {
        return this == SETTLED || this == FAILED || this == VOID;
    }
}
