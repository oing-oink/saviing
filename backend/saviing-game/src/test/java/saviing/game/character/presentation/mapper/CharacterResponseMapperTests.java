package saviing.game.character.presentation.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import saviing.game.character.application.dto.result.CharacterStatisticsResult;
import saviing.game.character.presentation.dto.response.CharacterStatisticsResponse;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CharacterResponseMapper 통계 관련 테스트 클래스
 * 통계 응답 DTO 매핑 기능을 검증합니다.
 */
class CharacterResponseMapperTests {

    private CharacterResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CharacterResponseMapper();
    }

    @Test
    void 캐릭터_통계_응답_매핑_성공() {
        // Given
        Long characterId = 1L;
        Integer petLevelSum = 150;

        Map<String, Map<String, Integer>> inventoryStatistics = new HashMap<>();
        inventoryStatistics.put("PET", Map.of("CAT", 12));
        inventoryStatistics.put("DECORATION", Map.of(
            "LEFT", 15,
            "RIGHT", 8,
            "BOTTOM", 6,
            "ROOM_COLOR", 4
        ));

        CharacterStatisticsResult result = CharacterStatisticsResult.builder()
            .characterId(characterId)
            .topPetLevelSum(petLevelSum)
            .inventoryRarityStatistics(inventoryStatistics)
            .build();

        // When
        CharacterStatisticsResponse response = mapper.toStatisticsResponse(result);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.characterId()).isEqualTo(characterId);
        assertThat(response.topPetLevelSum()).isEqualTo(petLevelSum);

        assertThat(response.inventoryRarityStatistics()).isNotNull();
        assertThat(response.inventoryRarityStatistics().pet())
            .containsEntry("CAT", 12);
        assertThat(response.inventoryRarityStatistics().decoration())
            .containsEntry("LEFT", 15)
            .containsEntry("RIGHT", 8)
            .containsEntry("BOTTOM", 6)
            .containsEntry("ROOM_COLOR", 4);
    }

    @Test
    void null_결과_매핑시_null_반환() {
        // Given
        CharacterStatisticsResult nullResult = null;

        // When
        CharacterStatisticsResponse response = mapper.toStatisticsResponse(nullResult);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void 빈_통계_데이터_매핑() {
        // Given
        Long characterId = 1L;
        Integer petLevelSum = 0;
        Map<String, Map<String, Integer>> emptyInventoryStatistics = new HashMap<>();

        CharacterStatisticsResult result = CharacterStatisticsResult.builder()
            .characterId(characterId)
            .topPetLevelSum(petLevelSum)
            .inventoryRarityStatistics(emptyInventoryStatistics)
            .build();

        // When
        CharacterStatisticsResponse response = mapper.toStatisticsResponse(result);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.characterId()).isEqualTo(characterId);
        assertThat(response.topPetLevelSum()).isEqualTo(0);
        assertThat(response.inventoryRarityStatistics().pet()).isEmpty();
        assertThat(response.inventoryRarityStatistics().decoration()).isEmpty();
    }

    @Test
    void PET만_있는_통계_매핑() {
        // Given
        Long characterId = 1L;
        Integer petLevelSum = 100;

        Map<String, Map<String, Integer>> inventoryStatistics = new HashMap<>();
        inventoryStatistics.put("PET", Map.of("CAT", 20));
        // DECORATION 타입은 없음

        CharacterStatisticsResult result = CharacterStatisticsResult.builder()
            .characterId(characterId)
            .topPetLevelSum(petLevelSum)
            .inventoryRarityStatistics(inventoryStatistics)
            .build();

        // When
        CharacterStatisticsResponse response = mapper.toStatisticsResponse(result);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.inventoryRarityStatistics().pet())
            .containsEntry("CAT", 20);
        assertThat(response.inventoryRarityStatistics().decoration())
            .isEmpty();
    }

    @Test
    void DECORATION만_있는_통계_매핑() {
        // Given
        Long characterId = 1L;
        Integer petLevelSum = 50;

        Map<String, Map<String, Integer>> inventoryStatistics = new HashMap<>();
        inventoryStatistics.put("DECORATION", Map.of("LEFT", 25, "RIGHT", 20));
        // PET 타입은 없음

        CharacterStatisticsResult result = CharacterStatisticsResult.builder()
            .characterId(characterId)
            .topPetLevelSum(petLevelSum)
            .inventoryRarityStatistics(inventoryStatistics)
            .build();

        // When
        CharacterStatisticsResponse response = mapper.toStatisticsResponse(result);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.inventoryRarityStatistics().pet())
            .isEmpty();
        assertThat(response.inventoryRarityStatistics().decoration())
            .containsEntry("LEFT", 25)
            .containsEntry("RIGHT", 20);
    }

    @Test
    void null_인벤토리_통계_매핑() {
        // Given
        Long characterId = 1L;
        Integer petLevelSum = 100;

        CharacterStatisticsResult result = CharacterStatisticsResult.builder()
            .characterId(characterId)
            .topPetLevelSum(petLevelSum)
            .inventoryRarityStatistics(null)
            .build();

        // When
        CharacterStatisticsResponse response = mapper.toStatisticsResponse(result);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.inventoryRarityStatistics().pet()).isEmpty();
        assertThat(response.inventoryRarityStatistics().decoration()).isEmpty();
    }
}