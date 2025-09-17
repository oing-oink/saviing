package saviing.game.item.domain.model.enums;

import saviing.game.item.domain.model.enums.category.Category;

/**
 * 액세서리 아이템의 카테고리
 */
public enum Accessory implements Category {
    HAT;

    @Override
    public ItemType getItemType() {
        return ItemType.ACCESSORY;
    }
}