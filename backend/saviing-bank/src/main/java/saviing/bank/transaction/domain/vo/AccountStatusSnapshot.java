package saviing.bank.transaction.domain.vo;

/**
 * AccountInternalApi 응답에서 사용하는 계좌 상태 스냅샷.
 */
public enum AccountStatusSnapshot {
    ACTIVE,
    FROZEN,
    CLOSED;

    /**
     * 문자열 상태 값을 스냅샷 enum으로 변환한다.
     */
    public static AccountStatusSnapshot from(String status) {
        if (status == null) {
            throw new IllegalArgumentException("Account status must not be null");
        }
        return AccountStatusSnapshot.valueOf(status);
    }

    /**
     * 거래가 가능한 상태인지 여부를 확인한다.
     */
    public boolean canTransact() {
        return this == ACTIVE;
    }
}
