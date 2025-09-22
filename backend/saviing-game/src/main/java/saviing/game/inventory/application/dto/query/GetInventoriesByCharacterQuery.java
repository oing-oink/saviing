package saviing.game.inventory.application.dto.query;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.enums.ItemCategory;

/**
 * 캐릭터별 인벤토리 목록 조회 Query
 */
public record GetInventoriesByCharacterQuery(
    CharacterId characterId,
    InventoryType type,
    ItemCategory category,
    Boolean isUsed
) {
    /**
     * GetInventoriesByCharacterQuery를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @return GetInventoriesByCharacterQuery
     */
    public static GetInventoriesByCharacterQuery of(Long characterId) {
        return new GetInventoriesByCharacterQuery(CharacterId.of(characterId), null, null, null);
    }

    /**
     * 필터링 조건을 포함한 GetInventoriesByCharacterQuery를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param type 인벤토리 타입
     * @param category 아이템 카테고리
     * @param isUsed 사용 여부
     * @return GetInventoriesByCharacterQuery
     */
    public static GetInventoriesByCharacterQuery of(Long characterId, InventoryType type, ItemCategory category, Boolean isUsed) {
        return new GetInventoriesByCharacterQuery(CharacterId.of(characterId), type, category, isUsed);
    }
}