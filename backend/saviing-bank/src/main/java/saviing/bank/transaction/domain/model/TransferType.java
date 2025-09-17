package saviing.bank.transaction.domain.model;

/**
 * 송금 유형. 내부 계좌 간 송금과 외부 연동 케이스를 구분한다.
 */
public enum TransferType {
    INTERNAL,
    EXTERNAL_OUTBOUND,
    EXTERNAL_INBOUND
}
