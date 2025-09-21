package saviing.game.inventory.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.item.domain.model.vo.ItemId;

/**
 * 인벤토리 아이템 추가 Command
 * Shop에서 아이템 구매 시 인벤토리에 추가할 때 사용됩니다.
 */
@Builder
public record AddInventoryItemCommand(
    CharacterId characterId,
    ItemId itemId
) {

    /**
     * 인벤토리 아이템 추가 Command를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param itemId 아이템 ID
     * @return AddInventoryItemCommand 인스턴스
     */
    public static AddInventoryItemCommand of(CharacterId characterId, ItemId itemId) {
        return AddInventoryItemCommand.builder()
            .characterId(characterId)
            .itemId(itemId)
            .build();
    }
}