package saviing.game.item.domain.model.enums;

import saviing.game.item.domain.model.enums.category.Category;

/**
 * 액세서리 아이템의 카테고리
 */
public enum Accessory implements Category {
    HAT("모자");

    private final String displayName;

    Accessory(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.ACCESSORY;
    }
}