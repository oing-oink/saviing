package saviing.game.item.application.dto.enums;

/**
 * 정렬 방향 열거형
 */
public enum SortDirection {
    ASC("asc"),
    DESC("desc");

    private final String value;

    SortDirection(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}