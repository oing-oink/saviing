package saviing.game.item.domain.model.enums;

import saviing.game.item.domain.model.enums.category.Category;

/**
 * 펫 아이템의 카테고리
 */
public enum Pet implements Category {
    CAT;

    @Override
    public ItemType getItemType() {
        return ItemType.PET;
    }
}