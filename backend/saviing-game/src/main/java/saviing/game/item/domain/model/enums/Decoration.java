package saviing.game.item.domain.model.enums;

import saviing.game.item.domain.model.enums.category.Category;

/**
 * 데코레이션 아이템의 카테고리
 */
public enum Decoration implements Category {
    LEFT("왼쪽 장식"),
    RIGHT("오른쪽 장식"),
    BOTTOM("하단 장식"),
    ROOM_COLOR("방 색상");

    private final String displayName;

    Decoration(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.DECORATION;
    }
}