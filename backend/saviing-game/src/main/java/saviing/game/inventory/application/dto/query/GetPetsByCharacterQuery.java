package saviing.game.inventory.application.dto.query;

import saviing.game.character.domain.model.vo.CharacterId;

/**
 * 캐릭터별 펫 목록 조회 Query
 */
public record GetPetsByCharacterQuery(
    CharacterId characterId
) {
    /**
     * GetPetsByCharacterQuery를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @return GetPetsByCharacterQuery
     */
    public static GetPetsByCharacterQuery of(Long characterId) {
        return new GetPetsByCharacterQuery(CharacterId.of(characterId));
    }
}