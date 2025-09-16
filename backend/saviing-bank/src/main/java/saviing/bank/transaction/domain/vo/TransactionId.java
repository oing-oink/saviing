package saviing.bank.transaction.domain.vo;

public record TransactionId(Long value) {

    public TransactionId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("거래 ID는 양수여야 합니다: " + value);
        }
    }

    public static TransactionId of(Long value) {
        return new TransactionId(value);
    }
}