package saviing.game.character.application.dto.query;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;

/**
 * 캐릭터 조회 Query
 */
@Builder
public record GetCharacterQuery(
    CharacterId characterId
) {
    /**
     * GetCharacterQuery를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @return GetCharacterQuery
     */
    public static GetCharacterQuery of(Long characterId) {
        return new GetCharacterQuery(new CharacterId(characterId));
    }
}