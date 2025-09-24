package saviing.game.character.presentation.mapper;

import org.springframework.stereotype.Component;
import saviing.game.character.application.dto.result.CharacterCreatedResult;
import saviing.game.character.application.dto.result.CharacterListResult;
import saviing.game.character.application.dto.result.CharacterResult;
import saviing.game.character.application.dto.result.CharacterStatisticsResult;
import saviing.game.character.presentation.dto.response.CharacterResponse;
import saviing.game.character.presentation.dto.response.CharacterStatisticsResponse;

import java.util.List;
import java.util.Map;

/**
 * Application Result를 CharacterResponse DTO로 변환하는 Mapper
 * Presentation 계층에서 Application Result를 API 응답용 DTO로 변환합니다.
 */
@Component
public class CharacterResponseMapper {

    /**
     * CharacterResult를 CharacterResponse로 변환합니다.
     *
     * @param result CharacterResult
     * @return CharacterResponse DTO
     */
    public CharacterResponse toResponse(CharacterResult result) {
        if (result == null) {
            return null;
        }

        return CharacterResponse.builder()
            .characterId(result.characterId())
            .customerId(result.customerId())
            .accountId(result.accountId())
            .connectionStatus(result.connectionStatus())
            .connectionDate(result.connectionDate())
            .terminationReason(result.terminationReason())
            .terminatedAt(result.terminatedAt())
            .coin(result.coin())
            .fishCoin(result.fishCoin())
            .roomCount(result.roomCount())
            .isActive(result.isActive())
            .deactivatedAt(result.deactivatedAt())
            .createdAt(result.createdAt())
            .updatedAt(result.updatedAt())
            .build();
    }

    /**
     * CharacterCreatedResult를 CharacterResponse로 변환합니다.
     *
     * @param result CharacterCreatedResult
     * @return CharacterResponse DTO
     */
    public CharacterResponse toResponse(CharacterCreatedResult result) {
        if (result == null) {
            return null;
        }

        return CharacterResponse.builder()
            .characterId(result.characterId())
            .customerId(result.customerId())
            .roomId(result.roomId())
            .coin(result.coin())
            .fishCoin(result.fishCoin())
            .roomCount(result.roomCount())
            .isActive(result.isActive())
            .connectionStatus(result.connectionStatus())
            .createdAt(result.createdAt())
            .updatedAt(result.updatedAt())
            .build();
    }

    /**
     * CharacterListResult를 CharacterResponse 목록으로 변환합니다.
     *
     * @param result CharacterListResult
     * @return CharacterResponse DTO 목록
     */
    public List<CharacterResponse> toResponse(CharacterListResult result) {
        if (result == null) {
            return List.of();
        }
        if (result.characters() == null) {
            return List.of();
        }

        return result.characters().stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * CharacterStatisticsResult를 CharacterStatisticsResponse로 변환합니다.
     * Application Result의 two-depth Map 구조를 Presentation Response의 구조화된 DTO로 변환합니다.
     *
     * @param result CharacterStatisticsResult
     * @return CharacterStatisticsResponse DTO
     */
    public CharacterStatisticsResponse toStatisticsResponse(CharacterStatisticsResult result) {
        if (result == null) {
            return null;
        }

        CharacterStatisticsResponse.InventoryRarityStatisticsResponse inventoryStats =
            toInventoryRarityStatisticsResponse(result.inventoryRarityStatistics());

        return CharacterStatisticsResponse.builder()
            .characterId(result.characterId())
            .topPetLevelSum(result.topPetLevelSum())
            .inventoryRarityStatistics(inventoryStats)
            .build();
    }

    /**
     * two-depth Map을 InventoryRarityStatisticsResponse로 변환합니다.
     * ItemType별로 그룹화된 통계 맵을 구조화된 응답 DTO로 변환합니다.
     *
     * @param statisticsMap ItemType별로 그룹화된 통계 맵
     * @return InventoryRarityStatisticsResponse DTO
     */
    private CharacterStatisticsResponse.InventoryRarityStatisticsResponse toInventoryRarityStatisticsResponse(
        Map<String, Map<String, Integer>> statisticsMap
    ) {
        if (statisticsMap == null || statisticsMap.isEmpty()) {
            return CharacterStatisticsResponse.InventoryRarityStatisticsResponse.empty();
        }

        Map<String, Integer> petStats = statisticsMap.getOrDefault("PET", Map.of());
        Map<String, Integer> decorationStats = statisticsMap.getOrDefault("DECORATION", Map.of());

        return CharacterStatisticsResponse.InventoryRarityStatisticsResponse.of(
            petStats,
            decorationStats
        );
    }
}