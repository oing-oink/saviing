package saviing.game.inventory.application.dto.query;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.item.domain.model.enums.Decoration;

/**
 * 캐릭터별 데코레이션 목록 조회 Query
 */
public record GetDecorationsByCharacterQuery(
    CharacterId characterId,
    Decoration category
) {
    /**
     * GetDecorationsByCharacterQuery를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param category 데코레이션 카테고리 (null이면 전체 조회)
     * @return GetDecorationsByCharacterQuery
     */
    public static GetDecorationsByCharacterQuery of(Long characterId, Decoration category) {
        return new GetDecorationsByCharacterQuery(CharacterId.of(characterId), category);
    }

    /**
     * 전체 데코레이션 조회 Query를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @return GetDecorationsByCharacterQuery
     */
    public static GetDecorationsByCharacterQuery allCategories(Long characterId) {
        return new GetDecorationsByCharacterQuery(CharacterId.of(characterId), null);
    }
}