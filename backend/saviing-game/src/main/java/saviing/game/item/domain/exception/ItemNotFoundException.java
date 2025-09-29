package saviing.game.item.domain.exception;

import saviing.game.item.domain.model.vo.ItemId;

/**
 * 아이템을 찾을 수 없을 때 발생하는 예외
 */
public class ItemNotFoundException extends ItemException {

    public ItemNotFoundException() {
        super(ItemErrorCode.ITEM_NOT_FOUND);
    }

    public ItemNotFoundException(String message) {
        super(ItemErrorCode.ITEM_NOT_FOUND, message);
    }

    /**
     * 아이템 ID로 ItemNotFoundException을 생성합니다.
     *
     * @param itemId 찾을 수 없는 아이템 ID
     * @return ItemNotFoundException 인스턴스
     */
    public static ItemNotFoundException withItemId(ItemId itemId) {
        return new ItemNotFoundException(
            String.format("아이템을 찾을 수 없습니다. 아이템 ID: %d", itemId.value())
        );
    }

    /**
     * 아이템 타입으로 ItemNotFoundException을 생성합니다.
     *
     * @param itemType 찾을 수 없는 아이템 타입
     * @return ItemNotFoundException 인스턴스
     */
    public static ItemNotFoundException withItemType(String itemType) {
        return new ItemNotFoundException(
            String.format("해당 타입의 아이템을 찾을 수 없습니다. 아이템 타입: %s", itemType)
        );
    }
}