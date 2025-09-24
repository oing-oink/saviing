package saviing.game.character.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Map;

/**
 * 캐릭터 통계 정보 API 응답 DTO
 * 캐릭터의 펫 레벨 통계와 인벤토리 희귀도 통계를 포함합니다.
 */
@Builder
@Schema(description = "캐릭터 통계 정보 응답")
public record CharacterStatisticsResponse(
    @Schema(description = "캐릭터 ID", example = "1")
    Long characterId,

    @Schema(description = "상위 펫들의 레벨 합계 (상위 10개)", example = "150")
    Integer topPetLevelSum,

    @Schema(description = "인벤토리 희귀도 통계 (ItemType별 카테고리 그룹)")
    InventoryRarityStatisticsResponse inventoryRarityStatistics
) {

    /**
     * 캐릭터 통계 정보를 기반으로 CharacterStatisticsResponse를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param topPetLevelSum 상위 펫들의 레벨 합계
     * @param inventoryRarityStatistics 인벤토리 희귀도 통계
     * @return CharacterStatisticsResponse 인스턴스
     */
    public static CharacterStatisticsResponse of(
        Long characterId,
        Integer topPetLevelSum,
        InventoryRarityStatisticsResponse inventoryRarityStatistics
    ) {
        return CharacterStatisticsResponse.builder()
            .characterId(characterId)
            .topPetLevelSum(topPetLevelSum != null ? topPetLevelSum : 0)
            .inventoryRarityStatistics(
                inventoryRarityStatistics != null ? inventoryRarityStatistics :
                    InventoryRarityStatisticsResponse.empty()
            )
            .build();
    }

    /**
     * 인벤토리 희귀도 통계 응답 DTO
     * ItemType별로 그룹화된 카테고리별 희귀도 합계 정보를 담습니다.
     */
    @Builder
    @Schema(description = "인벤토리 희귀도 통계")
    public record InventoryRarityStatisticsResponse(
        @Schema(
            description = "PET 타입 카테고리별 희귀도 합계",
            example = "{\"CAT\": 12}"
        )
        Map<String, Integer> pet,

        @Schema(
            description = "DECORATION 타입 카테고리별 희귀도 합계",
            example = "{\"LEFT\": 15, \"RIGHT\": 8, \"BOTTOM\": 6, \"ROOM_COLOR\": 4}"
        )
        Map<String, Integer> decoration
    ) {

        /**
         * ItemType별 희귀도 통계를 기반으로 InventoryRarityStatisticsResponse를 생성합니다.
         *
         * @param pet PET 타입 카테고리별 희귀도 합계
         * @param decoration DECORATION 타입 카테고리별 희귀도 합계
         * @return InventoryRarityStatisticsResponse 인스턴스
         */
        public static InventoryRarityStatisticsResponse of(
            Map<String, Integer> pet,
            Map<String, Integer> decoration
        ) {
            return InventoryRarityStatisticsResponse.builder()
                .pet(pet != null ? pet : Map.of())
                .decoration(decoration != null ? decoration : Map.of())
                .build();
        }

        /**
         * 빈 통계 정보를 가진 InventoryRarityStatisticsResponse를 생성합니다.
         *
         * @return 빈 통계를 가진 InventoryRarityStatisticsResponse 인스턴스
         */
        public static InventoryRarityStatisticsResponse empty() {
            return InventoryRarityStatisticsResponse.builder()
                .pet(Map.of())
                .decoration(Map.of())
                .build();
        }
    }
}