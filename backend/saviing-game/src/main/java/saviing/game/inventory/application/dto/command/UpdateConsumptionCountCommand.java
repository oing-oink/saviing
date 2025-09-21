package saviing.game.inventory.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.item.domain.model.vo.ItemId;

/**
 * 소모품 개수 업데이트 Command
 * 소모품 사용 또는 획득 시 개수를 증감할 때 사용됩니다.
 */
@Builder
public record UpdateConsumptionCountCommand(
    CharacterId characterId,
    ItemId itemId,
    Integer countChange
) {

    /**
     * 소모품 개수 업데이트 Command를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param itemId 아이템 ID
     * @param countChange 개수 변화량 (양수: 증가, 음수: 감소)
     * @return UpdateConsumptionCountCommand 인스턴스
     */
    public static UpdateConsumptionCountCommand of(
        CharacterId characterId,
        ItemId itemId,
        Integer countChange
    ) {
        return UpdateConsumptionCountCommand.builder()
            .characterId(characterId)
            .itemId(itemId)
            .countChange(countChange)
            .build();
    }

    /**
     * Command 유효성을 검증합니다.
     */
    public void validate() {
        if (characterId == null) {
            throw new IllegalArgumentException("캐릭터 ID는 필수입니다");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("아이템 ID는 필수입니다");
        }
        if (countChange == null || countChange == 0) {
            throw new IllegalArgumentException("개수 변화량은 0이 아닌 값이어야 합니다");
        }
    }
}