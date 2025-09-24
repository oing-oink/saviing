package saviing.game.character.application.dto.result;

import lombok.Builder;

import java.util.Map;

/**
 * 캐릭터 통계 조회 결과 Result
 * 캐릭터의 펫 레벨 통계와 인벤토리 희귀도 통계를 포함합니다.
 */
@Builder
public record CharacterStatisticsResult(
    Long characterId,
    Integer topPetLevelSum,
    Map<String, Map<String, Integer>> inventoryRarityStatistics
) {

    /**
     * 캐릭터 통계 정보를 기반으로 CharacterStatisticsResult를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param topPetLevelSum 상위 펫들의 레벨 합계
     * @param inventoryRarityStatistics ItemType별로 그룹화된 인벤토리 희귀도 통계
     * @return CharacterStatisticsResult 인스턴스
     */
    public static CharacterStatisticsResult of(
        Long characterId,
        Integer topPetLevelSum,
        Map<String, Map<String, Integer>> inventoryRarityStatistics
    ) {
        return CharacterStatisticsResult.builder()
            .characterId(characterId)
            .topPetLevelSum(topPetLevelSum != null ? topPetLevelSum : 0)
            .inventoryRarityStatistics(
                inventoryRarityStatistics != null ? inventoryRarityStatistics : Map.of()
            )
            .build();
    }
}