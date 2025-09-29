package saviing.game.item.domain.exception;

import saviing.game.item.domain.model.vo.ItemId;

/**
 * 아이템 상태 변경 시 발생하는 예외
 */
public class ItemUnavailableException extends ItemException {

    public ItemUnavailableException() {
        super(ItemErrorCode.ITEM_ALREADY_UNAVAILABLE);
    }

    public ItemUnavailableException(String message) {
        super(ItemErrorCode.ITEM_ALREADY_UNAVAILABLE, message);
    }

    public ItemUnavailableException(ItemErrorCode errorCode, String message) {
        super(errorCode, message);
    }


    /**
     * 아이템이 이미 판매 중인 경우 예외를 생성합니다.
     *
     * @param itemId 아이템 ID
     * @return ItemUnavailableException 인스턴스
     */
    public static ItemUnavailableException alreadyAvailable(ItemId itemId) {
        return new ItemUnavailableException(ItemErrorCode.ITEM_ALREADY_AVAILABLE,
            String.format("이미 판매 중인 아이템입니다. 아이템 ID: %d", itemId.value())
        );
    }

    /**
     * 아이템이 이미 판매 중단된 경우 예외를 생성합니다.
     *
     * @param itemId 아이템 ID
     * @return ItemUnavailableException 인스턴스
     */
    public static ItemUnavailableException alreadyUnavailable(ItemId itemId) {
        return new ItemUnavailableException(ItemErrorCode.ITEM_ALREADY_UNAVAILABLE,
            String.format("이미 판매 중단된 아이템입니다. 아이템 ID: %d", itemId.value())
        );
    }

}