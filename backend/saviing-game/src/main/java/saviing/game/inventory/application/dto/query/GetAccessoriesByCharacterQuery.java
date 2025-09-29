package saviing.game.inventory.application.dto.query;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.item.domain.model.enums.Accessory;

/**
 * 캐릭터별 액세서리 목록 조회 Query
 */
public record GetAccessoriesByCharacterQuery(
    CharacterId characterId,
    Accessory category
) {
    /**
     * GetAccessoriesByCharacterQuery를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param category 액세서리 카테고리 (null이면 전체 조회)
     * @return GetAccessoriesByCharacterQuery
     */
    public static GetAccessoriesByCharacterQuery of(Long characterId, Accessory category) {
        return new GetAccessoriesByCharacterQuery(CharacterId.of(characterId), category);
    }

    /**
     * 전체 액세서리 조회 Query를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @return GetAccessoriesByCharacterQuery
     */
    public static GetAccessoriesByCharacterQuery allCategories(Long characterId) {
        return new GetAccessoriesByCharacterQuery(CharacterId.of(characterId), null);
    }
}