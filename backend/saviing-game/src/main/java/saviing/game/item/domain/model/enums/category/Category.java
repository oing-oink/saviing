package saviing.game.item.domain.model.enums.category;

import saviing.game.item.domain.model.enums.ItemType;

/**
 * 아이템 카테고리 인터페이스
 * 각 ItemType별 카테고리 enum들이 구현해야 하는 공통 메서드를 정의합니다.
 */
public interface Category {

    /**
     * 카테고리의 표시 이름을 반환합니다.
     *
     * @return 카테고리 표시 이름
     */
    String getDisplayName();

    /**
     * 카테고리가 속하는 아이템 타입을 반환합니다.
     *
     * @return 아이템 타입
     */
    ItemType getItemType();

    /**
     * 카테고리가 특정 아이템 타입에 속하는지 확인합니다.
     *
     * @param itemType 확인할 아이템 타입
     * @return 해당 타입에 속하는지 여부
     */
    default boolean belongsTo(ItemType itemType) {
        return getItemType() == itemType;
    }

    /**
     * 카테고리의 이름을 반환합니다 (enum 상수명).
     *
     * @return 카테고리 이름
     */
    String name();
}