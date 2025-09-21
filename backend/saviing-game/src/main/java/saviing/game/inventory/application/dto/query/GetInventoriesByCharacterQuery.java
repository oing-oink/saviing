package saviing.game.inventory.application.dto.query;

import saviing.game.character.domain.model.vo.CharacterId;

/**
 * 캐릭터별 인벤토리 목록 조회 Query
 */
public record GetInventoriesByCharacterQuery(
    CharacterId characterId
) {
    /**
     * GetInventoriesByCharacterQuery를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @return GetInventoriesByCharacterQuery
     */
    public static GetInventoriesByCharacterQuery of(Long characterId) {
        return new GetInventoriesByCharacterQuery(CharacterId.of(characterId));
    }
}