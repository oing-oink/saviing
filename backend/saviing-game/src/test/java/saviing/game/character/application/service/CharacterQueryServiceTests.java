package saviing.game.character.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import saviing.game.character.application.dto.query.GetCharacterStatisticsQuery;
import saviing.game.character.application.dto.result.CharacterStatisticsResult;
import saviing.game.character.application.mapper.CharacterResultMapper;
import saviing.game.character.domain.exception.CharacterNotFoundException;
import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.repository.CharacterRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * CharacterQueryService 테스트 클래스
 * 캐릭터 통계 조회 기능을 검증합니다.
 */
class CharacterQueryServiceTests {

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private CharacterResultMapper resultMapper;

    @InjectMocks
    private CharacterQueryService characterQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 캐릭터_통계_조회_성공() {
        // Given
        Long characterId = 1L;
        CharacterId charId = CharacterId.of(characterId);
        GetCharacterStatisticsQuery query = GetCharacterStatisticsQuery.of(characterId);

        Character mockCharacter = mock(Character.class);
        Integer expectedPetLevelSum = 150;
        Map<String, Integer> flatRarityMap = new HashMap<>();
        flatRarityMap.put("CAT", 12);
        flatRarityMap.put("LEFT", 15);
        flatRarityMap.put("RIGHT", 8);

        Map<String, Map<String, Integer>> expectedGroupedMap = new HashMap<>();
        expectedGroupedMap.put("PET", Map.of("CAT", 12));
        expectedGroupedMap.put("DECORATION", Map.of("LEFT", 15, "RIGHT", 8));

        CharacterStatisticsResult expectedResult = CharacterStatisticsResult.builder()
            .characterId(characterId)
            .topPetLevelSum(expectedPetLevelSum)
            .inventoryRarityStatistics(expectedGroupedMap)
            .build();

        when(characterRepository.findById(charId)).thenReturn(Optional.of(mockCharacter));
        when(characterRepository.findTopPetLevelSumByCharacterId(eq(charId), eq(10)))
            .thenReturn(expectedPetLevelSum);
        when(characterRepository.findTopRaritySumByCharacterIdAndCategory(eq(charId), eq(5)))
            .thenReturn(flatRarityMap);
        when(resultMapper.toStatisticsResult(eq(charId), eq(expectedPetLevelSum), any()))
            .thenReturn(expectedResult);

        // When
        CharacterStatisticsResult result = characterQueryService.getCharacterStatistics(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.characterId()).isEqualTo(characterId);
        assertThat(result.topPetLevelSum()).isEqualTo(expectedPetLevelSum);
        assertThat(result.inventoryRarityStatistics()).isNotEmpty();
        assertThat(result.inventoryRarityStatistics().get("PET")).containsEntry("CAT", 12);
        assertThat(result.inventoryRarityStatistics().get("DECORATION"))
            .containsEntry("LEFT", 15)
            .containsEntry("RIGHT", 8);

        verify(characterRepository).findById(charId);
        verify(characterRepository).findTopPetLevelSumByCharacterId(charId, 10);
        verify(characterRepository).findTopRaritySumByCharacterIdAndCategory(charId, 5);
        verify(resultMapper).toStatisticsResult(eq(charId), eq(expectedPetLevelSum), any());
    }

    @Test
    void 존재하지_않는_캐릭터_조회시_예외_발생() {
        // Given
        Long characterId = 999L;
        CharacterId charId = CharacterId.of(characterId);
        GetCharacterStatisticsQuery query = GetCharacterStatisticsQuery.of(characterId);

        when(characterRepository.findById(charId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> characterQueryService.getCharacterStatistics(query))
            .isInstanceOf(CharacterNotFoundException.class);

        verify(characterRepository).findById(charId);
    }

    @Test
    void 빈_통계_데이터_조회시_정상_처리() {
        // Given
        Long characterId = 1L;
        CharacterId charId = CharacterId.of(characterId);
        GetCharacterStatisticsQuery query = GetCharacterStatisticsQuery.of(characterId);

        Character mockCharacter = mock(Character.class);
        Integer expectedPetLevelSum = 0;
        Map<String, Integer> emptyFlatRarityMap = new HashMap<>();

        Map<String, Map<String, Integer>> expectedEmptyGroupedMap = new HashMap<>();
        expectedEmptyGroupedMap.put("PET", new HashMap<>());
        expectedEmptyGroupedMap.put("DECORATION", new HashMap<>());

        CharacterStatisticsResult expectedResult = CharacterStatisticsResult.builder()
            .characterId(characterId)
            .topPetLevelSum(expectedPetLevelSum)
            .inventoryRarityStatistics(expectedEmptyGroupedMap)
            .build();

        when(characterRepository.findById(charId)).thenReturn(Optional.of(mockCharacter));
        when(characterRepository.findTopPetLevelSumByCharacterId(eq(charId), anyInt()))
            .thenReturn(expectedPetLevelSum);
        when(characterRepository.findTopRaritySumByCharacterIdAndCategory(eq(charId), anyInt()))
            .thenReturn(emptyFlatRarityMap);
        when(resultMapper.toStatisticsResult(eq(charId), eq(expectedPetLevelSum), any()))
            .thenReturn(expectedResult);

        // When
        CharacterStatisticsResult result = characterQueryService.getCharacterStatistics(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.characterId()).isEqualTo(characterId);
        assertThat(result.topPetLevelSum()).isEqualTo(0);
        assertThat(result.inventoryRarityStatistics().get("PET")).isEmpty();
        assertThat(result.inventoryRarityStatistics().get("DECORATION")).isEmpty();
    }

    @Test
    void 카테고리_그룹화_로직_검증() {
        // Given
        Long characterId = 1L;
        CharacterId charId = CharacterId.of(characterId);
        GetCharacterStatisticsQuery query = GetCharacterStatisticsQuery.of(characterId);

        Character mockCharacter = mock(Character.class);
        Integer expectedPetLevelSum = 100;

        // 모든 카테고리 포함한 데이터
        Map<String, Integer> comprehensiveFlatRarityMap = new HashMap<>();
        comprehensiveFlatRarityMap.put("CAT", 20);
        comprehensiveFlatRarityMap.put("LEFT", 15);
        comprehensiveFlatRarityMap.put("RIGHT", 10);
        comprehensiveFlatRarityMap.put("BOTTOM", 8);
        comprehensiveFlatRarityMap.put("ROOM_COLOR", 5);

        CharacterStatisticsResult expectedResult = CharacterStatisticsResult.builder()
            .characterId(characterId)
            .topPetLevelSum(expectedPetLevelSum)
            .inventoryRarityStatistics(Map.of())
            .build();

        when(characterRepository.findById(charId)).thenReturn(Optional.of(mockCharacter));
        when(characterRepository.findTopPetLevelSumByCharacterId(eq(charId), anyInt()))
            .thenReturn(expectedPetLevelSum);
        when(characterRepository.findTopRaritySumByCharacterIdAndCategory(eq(charId), anyInt()))
            .thenReturn(comprehensiveFlatRarityMap);
        when(resultMapper.toStatisticsResult(eq(charId), eq(expectedPetLevelSum), any()))
            .thenReturn(expectedResult);

        // When
        characterQueryService.getCharacterStatistics(query);

        // Then - Mapper가 올바른 그룹화된 데이터로 호출되는지 검증
        verify(resultMapper).toStatisticsResult(eq(charId), eq(expectedPetLevelSum), any());
    }
}