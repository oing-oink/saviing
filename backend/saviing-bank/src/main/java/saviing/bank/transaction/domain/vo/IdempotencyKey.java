package saviing.bank.transaction.domain.vo;

public record IdempotencyKey(String value) {

    public IdempotencyKey {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("멱등키는 비어있을 수 없습니다");
        }
        if (value.length() > 64) {
            throw new IllegalArgumentException("멱등키는 64자를 초과할 수 없습니다: " + value.length());
        }
    }

    public static IdempotencyKey of(String value) {
        return new IdempotencyKey(value.trim());
    }
}