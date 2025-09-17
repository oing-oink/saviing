package saviing.bank.transaction.domain.vo;

/**
 * 거래 식별자를 나타내는 값 객체
 * 거래의 고유한 식별자를 안전하게 처리한다.
 */
public record TransactionId(Long value) {

    public TransactionId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("거래 ID는 양수여야 합니다: " + value);
        }
    }

    /**
     * 주어진 Long 값으로 거래 ID를 생성한다
     *
     * @param value 거래 ID 값 (양수여야 함)
     * @return 생성된 거래 ID 객체
     * @throws IllegalArgumentException ID가 null이거나 양수가 아닌 경우
     */
    public static TransactionId of(Long value) {
        return new TransactionId(value);
    }
}