package saviing.game.character.application.dto.query;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;

/**
 * 캐릭터 통계 조회 Query
 * 캐릭터의 펫 레벨 합계와 인벤토리 희귀도 통계를 조회하기 위한 Query입니다.
 */
@Builder
public record GetCharacterStatisticsQuery(
    CharacterId characterId
) {

    /**
     * 캐릭터 ID를 기반으로 GetCharacterStatisticsQuery를 생성합니다.
     *
     * @param characterId 캐릭터 ID (Long)
     * @return GetCharacterStatisticsQuery 인스턴스
     */
    public static GetCharacterStatisticsQuery of(Long characterId) {
        return new GetCharacterStatisticsQuery(CharacterId.of(characterId));
    }
}