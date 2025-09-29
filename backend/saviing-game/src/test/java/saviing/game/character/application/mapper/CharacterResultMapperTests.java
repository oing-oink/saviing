package saviing.game.character.application.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import saviing.game.character.application.dto.result.CharacterStatisticsResult;
import saviing.game.character.domain.model.vo.CharacterId;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CharacterResultMapper 테스트 클래스
 * 캐릭터 통계 매핑 기능을 검증합니다.
 */
class CharacterResultMapperTests {

    private CharacterResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CharacterResultMapper();
    }

    @Test
    void 캐릭터_통계_매핑_성공() {
        // Given
        Long characterId = 1L;
        CharacterId charId = CharacterId.of(characterId);
        Integer petLevelSum = 150;

        Map<String, Map<String, Integer>> inventoryStatistics = new HashMap<>();
        inventoryStatistics.put("PET", Map.of("CAT", 12));
        inventoryStatistics.put("DECORATION", Map.of("LEFT", 15, "RIGHT", 8));

        // When
        CharacterStatisticsResult result = mapper.toStatisticsResult(
            charId,
            petLevelSum,
            inventoryStatistics
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.characterId()).isEqualTo(characterId);
        assertThat(result.topPetLevelSum()).isEqualTo(petLevelSum);
        assertThat(result.inventoryRarityStatistics()).isEqualTo(inventoryStatistics);
        assertThat(result.inventoryRarityStatistics().get("PET")).containsEntry("CAT", 12);
        assertThat(result.inventoryRarityStatistics().get("DECORATION"))
            .containsEntry("LEFT", 15)
            .containsEntry("RIGHT", 8);
    }

    @Test
    void null_파라미터_처리() {
        // Given
        CharacterId nullCharacterId = null;
        Integer nullPetLevelSum = null;
        Map<String, Map<String, Integer>> nullInventoryStatistics = null;

        // When
        CharacterStatisticsResult result = mapper.toStatisticsResult(
            nullCharacterId,
            nullPetLevelSum,
            nullInventoryStatistics
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.characterId()).isNull();
        assertThat(result.topPetLevelSum()).isEqualTo(0);
        assertThat(result.inventoryRarityStatistics()).isEmpty();
    }

    @Test
    void 빈_통계_데이터_매핑() {
        // Given
        Long characterId = 1L;
        CharacterId charId = CharacterId.of(characterId);
        Integer zeroPetLevelSum = 0;
        Map<String, Map<String, Integer>> emptyInventoryStatistics = new HashMap<>();

        // When
        CharacterStatisticsResult result = mapper.toStatisticsResult(
            charId,
            zeroPetLevelSum,
            emptyInventoryStatistics
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.characterId()).isEqualTo(characterId);
        assertThat(result.topPetLevelSum()).isEqualTo(0);
        assertThat(result.inventoryRarityStatistics()).isEmpty();
    }

    @Test
    void 복잡한_통계_데이터_매핑() {
        // Given
        Long characterId = 1L;
        CharacterId charId = CharacterId.of(characterId);
        Integer petLevelSum = 500;

        Map<String, Map<String, Integer>> complexInventoryStatistics = new HashMap<>();
        complexInventoryStatistics.put("PET", Map.of("CAT", 25));
        complexInventoryStatistics.put("DECORATION", Map.of(
            "LEFT", 20,
            "RIGHT", 18,
            "BOTTOM", 15,
            "ROOM_COLOR", 12
        ));

        // When
        CharacterStatisticsResult result = mapper.toStatisticsResult(
            charId,
            petLevelSum,
            complexInventoryStatistics
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.characterId()).isEqualTo(characterId);
        assertThat(result.topPetLevelSum()).isEqualTo(petLevelSum);

        Map<String, Integer> petStats = result.inventoryRarityStatistics().get("PET");
        assertThat(petStats).containsEntry("CAT", 25);

        Map<String, Integer> decorationStats = result.inventoryRarityStatistics().get("DECORATION");
        assertThat(decorationStats)
            .containsEntry("LEFT", 20)
            .containsEntry("RIGHT", 18)
            .containsEntry("BOTTOM", 15)
            .containsEntry("ROOM_COLOR", 12);
    }
}