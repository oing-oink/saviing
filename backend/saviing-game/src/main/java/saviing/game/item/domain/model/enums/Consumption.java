package saviing.game.item.domain.model.enums;

import saviing.game.item.domain.model.enums.category.Category;

/**
 * 소모품 카테고리 열거형
 * 소모품 아이템의 세부 분류를 정의합니다.
 */
public enum Consumption implements Category {
    /**
     * 장난감
     * 펫과 놀아줄 수 있는 소모품
     */
    TOY,

    /**
     * 먹이
     * 펫에게 줄 수 있는 먹이 소모품
     */
    FOOD;

    @Override
    public ItemType getItemType() {
        return ItemType.CONSUMPTION;
    }
}