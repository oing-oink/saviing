package saviing.game.item.domain.model.vo;

/**
 * 아이템 설명 Value Object
 * 아이템 설명의 유효성을 검증합니다.
 */
public record ItemDescription(
    String value
) {
    private static final int MAX_LENGTH = 500;

    public ItemDescription {
        if (value == null) {
            value = "";
        } else {
            value = value.trim();
            if (value.length() > MAX_LENGTH) {
                throw new IllegalArgumentException(
                    String.format("아이템 설명은 %d자 이하여야 합니다", MAX_LENGTH)
                );
            }
        }
    }

    /**
     * String 값으로 ItemDescription을 생성합니다.
     *
     * @param value 아이템 설명
     * @return ItemDescription 인스턴스
     */
    public static ItemDescription of(String value) {
        return new ItemDescription(value);
    }

    /**
     * 빈 설명을 생성합니다.
     *
     * @return 빈 ItemDescription 인스턴스
     */
    public static ItemDescription empty() {
        return new ItemDescription("");
    }

    /**
     * 설명이 비어있는지 확인합니다.
     *
     * @return 설명이 비어있는지 여부
     */
    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * 설명의 길이를 반환합니다.
     *
     * @return 설명 길이
     */
    public int length() {
        return value.length();
    }
}