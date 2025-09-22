package saviing.game.inventory.domain.exception;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.item.domain.model.vo.ItemId;

/**
 * 중복 인벤토리 아이템 예외
 * 이미 존재하는 인벤토리 아이템을 추가하려고 할 때 발생합니다.
 */
public class DuplicateInventoryItemException extends InventoryException {

    public DuplicateInventoryItemException(CharacterId characterId, ItemId itemId) {
        super(
            InventoryErrorCode.DUPLICATE_INVENTORY_ITEM,
            "이미 존재하는 인벤토리 아이템입니다. character=" + characterId + ", item=" + itemId
        );
    }

    /**
     * 중복 인벤토리 아이템 예외를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param itemId 아이템 ID
     * @return DuplicateInventoryItemException 인스턴스
     */
    public static DuplicateInventoryItemException of(CharacterId characterId, ItemId itemId) {
        return new DuplicateInventoryItemException(characterId, itemId);
    }
}