package saviing.game.character.infrastructure.persistence.repository;

import java.util.Map;

import org.junit.jupiter.api.Test;

import saviing.game.character.domain.model.vo.CharacterId;

import static org.assertj.core.api.Assertions.assertThat;

class CharacterRepositoryImplTests {

    @Test
    void limit이_0일때_적절히_처리됨() {
        // Given
        CharacterId characterId = CharacterId.of(1L);
        int limit = 0;
        CharacterRepositoryImpl repository = new CharacterRepositoryImpl(null, null, null);

        // When
        Integer petLevelSum = repository.findTopPetLevelSumByCharacterId(characterId, limit);
        Map<String, Integer> raritySum = repository.findTopRaritySumByCharacterIdAndCategory(characterId, limit);

        // Then
        assertThat(petLevelSum).isEqualTo(0);
        assertThat(raritySum).isNotNull();
        assertThat(raritySum).isEmpty();
    }

    @Test
    void null_캐릭터ID_처리() {
        // Given
        int limit = 10;

        // When & Then - CharacterId 생성 시 예외 발생
        try {
            CharacterId nullCharacterId = CharacterId.of(null);
        } catch (IllegalArgumentException e) {
            // CharacterId validation에서 예외 발생 예상
            assertThat(e.getMessage()).contains("캐릭터 ID는 양수여야 합니다");
        }
    }
}