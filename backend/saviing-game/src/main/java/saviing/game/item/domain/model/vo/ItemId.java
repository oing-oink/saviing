package saviing.game.item.domain.model.vo;

/**
 * 아이템 식별자 Value Object
 */
public record ItemId(
    Long value
) {
    public ItemId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("아이템 ID는 양수여야 합니다");
        }
    }

    /**
     * Long 값으로 ItemId를 생성합니다.
     *
     * @param value 아이템 ID 값
     * @return ItemId 인스턴스
     */
    public static ItemId of(Long value) {
        return new ItemId(value);
    }
}