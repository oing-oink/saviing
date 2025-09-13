package saviing.game.character.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;

/**
 * 방 수 증가 Command
 */
@Builder
public record IncreaseRoomCountCommand(
    CharacterId characterId
) {
    /**
     * IncreaseRoomCountCommand를 생성합니다.
     * 
     * @param characterId 캐릭터 ID
     * @return IncreaseRoomCountCommand
     */
    public static IncreaseRoomCountCommand of(CharacterId characterId) {
        return new IncreaseRoomCountCommand(characterId);
    }
}