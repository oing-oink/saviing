package saviing.game.character.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;

/**
 * 계좌 해지 처리 Command
 * 외부 도메인(Bank)에서 계좌가 해지되었을 때 Character 도메인에 알리기 위한 Command
 */
@Builder
public record HandleAccountTerminatedCommand(
    CharacterId characterId,
    String terminationReason
) {
    public HandleAccountTerminatedCommand {
        if (terminationReason == null || terminationReason.trim().isEmpty()) {
            throw new IllegalArgumentException("해지 사유는 비어있을 수 없습니다");
        }
    }
    
    /**
     * HandleAccountTerminatedCommand를 생성합니다.
     * 
     * @param characterId 캐릭터 ID
     * @param terminationReason 해지 사유
     * @return HandleAccountTerminatedCommand
     */
    public static HandleAccountTerminatedCommand of(CharacterId characterId, String terminationReason) {
        return new HandleAccountTerminatedCommand(characterId, terminationReason);
    }
}