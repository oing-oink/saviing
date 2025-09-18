package saviing.game.item.application.dto.enums;

/**
 * 아이템 정렬 필드 열거형
 */
public enum SortField {
    NAME("name"),
    PRICE("price"),
    RARITY("rarity"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String value;

    SortField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}