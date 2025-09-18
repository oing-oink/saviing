package saviing.game.item.domain.model.vo;

/**
 * 아이템 이름 Value Object
 * 아이템 이름의 유효성을 검증합니다.
 */
public record ItemName(
    String value
) {
    private static final int MAX_LENGTH = 100;
    private static final int MIN_LENGTH = 1;

    public ItemName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("아이템 이름은 비어있을 수 없습니다");
        }

        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("아이템 이름은 %d자 이상 %d자 이하여야 합니다", MIN_LENGTH, MAX_LENGTH)
            );
        }
    }

    /**
     * String 값으로 ItemName을 생성합니다.
     *
     * @param value 아이템 이름
     * @return ItemName 인스턴스
     */
    public static ItemName of(String value) {
        return new ItemName(value);
    }

    /**
     * 아이템 이름의 길이를 반환합니다.
     *
     * @return 이름 길이
     */
    public int length() {
        return value.length();
    }
}