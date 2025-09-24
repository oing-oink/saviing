package saviing.game.character.presentation.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import saviing.game.character.application.dto.result.CharacterStatisticsResult;
import saviing.game.character.application.service.CharacterCommandService;
import saviing.game.character.application.service.CharacterQueryService;
import saviing.game.character.domain.exception.CharacterNotFoundException;
import saviing.game.character.presentation.dto.response.CharacterStatisticsResponse;
import saviing.game.character.presentation.mapper.CharacterRequestMapper;
import saviing.game.character.presentation.mapper.CharacterResponseMapper;
import saviing.common.config.JwtConfig;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CharacterController 통합 테스트 클래스
 * 캐릭터 통계 조회 API 테스트를 포함합니다.
 */
@WebMvcTest(CharacterController.class)
class CharacterControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CharacterCommandService characterCommandService;

    @MockBean
    private CharacterQueryService characterQueryService;

    @MockBean
    private CharacterRequestMapper requestMapper;

    @MockBean
    private CharacterResponseMapper responseMapper;

    @MockBean
    private JwtConfig jwtConfig;

    @Test
    @WithMockUser
    void 캐릭터_통계_조회_성공() throws Exception {
        // Given
        Long characterId = 1L;
        Integer expectedPetLevelSum = 150;

        Map<String, Map<String, Integer>> expectedInventoryStatistics = new HashMap<>();
        expectedInventoryStatistics.put("PET", Map.of("CAT", 12));
        expectedInventoryStatistics.put("DECORATION", Map.of(
            "LEFT", 15,
            "RIGHT", 8,
            "BOTTOM", 6,
            "ROOM_COLOR", 4
        ));

        CharacterStatisticsResult mockResult = CharacterStatisticsResult.builder()
            .characterId(characterId)
            .topPetLevelSum(expectedPetLevelSum)
            .inventoryRarityStatistics(expectedInventoryStatistics)
            .build();

        CharacterStatisticsResponse mockResponse = CharacterStatisticsResponse.builder()
            .characterId(characterId)
            .topPetLevelSum(expectedPetLevelSum)
            .inventoryRarityStatistics(CharacterStatisticsResponse.InventoryRarityStatisticsResponse.of(
                Map.of("CAT", 12),
                Map.of("LEFT", 15, "RIGHT", 8, "BOTTOM", 6, "ROOM_COLOR", 4)
            ))
            .build();

        when(characterQueryService.getCharacterStatistics(any())).thenReturn(mockResult);
        when(responseMapper.toStatisticsResponse(mockResult)).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/v1/game/characters/{characterId}/statistics", characterId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.body.characterId").value(1))
            .andExpect(jsonPath("$.body.topPetLevelSum").value(150));
    }

    @Test
    @WithMockUser
    void 존재하지_않는_캐릭터_통계_조회시_404_반환() throws Exception {
        // Given
        Long nonExistentCharacterId = 999L;

        when(characterQueryService.getCharacterStatistics(any()))
            .thenThrow(new CharacterNotFoundException(nonExistentCharacterId));

        // When & Then
        mockMvc.perform(get("/v1/game/characters/{characterId}/statistics", nonExistentCharacterId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void 잘못된_캐릭터ID_형식으로_통계_조회시_400_반환() throws Exception {
        // Given
        String invalidCharacterId = "invalid";

        // When & Then
        mockMvc.perform(get("/v1/game/characters/{characterId}/statistics", invalidCharacterId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void 음수_캐릭터ID로_통계_조회시_도메인_예외_발생() throws Exception {
        // Given
        Long negativeCharacterId = -1L;

        when(characterQueryService.getCharacterStatistics(any()))
            .thenThrow(new IllegalArgumentException("캐릭터 ID는 양수여야 합니다"));

        // When & Then
        mockMvc.perform(get("/v1/game/characters/{characterId}/statistics", negativeCharacterId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
}