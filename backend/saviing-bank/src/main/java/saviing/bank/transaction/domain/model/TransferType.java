package saviing.bank.transaction.domain.model;

/**
 * 송금 유형을 나타내는 열거형
 * 내부 계좌 간 송금과 외부 연동 케이스를 구분한다.
 */
public enum TransferType {
    /** 내부 계좌 간 송금 */
    INTERNAL,
    /** 외부로 나가는 송금 */
    EXTERNAL_OUTBOUND,
    /** 외부에서 들어오는 송금 */
    EXTERNAL_INBOUND
}
