package saviing.game.character.domain.model.vo;

/**
 * 고객 식별자 Value Object
 * Bank Server의 고객을 참조합니다.
 */
public record CustomerId(
    Long value
) {
    public CustomerId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Customer ID must be positive");
        }
    }

    /**
     * Long 값으로 CustomerId를 생성합니다.
     * 
     * @param value 고객 ID 값
     * @return CustomerId 인스턴스
     */
    public static CustomerId of(Long value) {
        return new CustomerId(value);
    }
}