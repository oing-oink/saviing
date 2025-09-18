package saviing.game.item.domain.model.enums;

import saviing.game.item.domain.model.enums.category.Category;

/**
 * 데코레이션 아이템의 카테고리
 */
public enum Decoration implements Category {
    LEFT,
    RIGHT,
    BOTTOM,
    ROOM_COLOR;

    @Override
    public ItemType getItemType() {
        return ItemType.DECORATION;
    }
}