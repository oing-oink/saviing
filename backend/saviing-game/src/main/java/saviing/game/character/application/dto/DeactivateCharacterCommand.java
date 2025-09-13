package saviing.game.character.application.dto;

import saviing.game.character.domain.model.vo.CharacterId;

/**
 * 캐릭터 비활성화 Command
 */
public record DeactivateCharacterCommand(
    CharacterId characterId
) {
    /**
     * DeactivateCharacterCommand를 생성합니다.
     * 
     * @param characterId 비활성화할 캐릭터 ID
     * @return DeactivateCharacterCommand
     */
    public static DeactivateCharacterCommand of(CharacterId characterId) {
        return new DeactivateCharacterCommand(characterId);
    }
}