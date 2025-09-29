package saviing.game.character.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;

/**
 * 계좌 연결 취소 Command
 */
@Builder
public record CancelAccountConnectionCommand(
    CharacterId characterId
) {
    /**
     * CancelAccountConnectionCommand를 생성합니다.
     * 
     * @param characterId 캐릭터 ID
     * @return CancelAccountConnectionCommand
     */
    public static CancelAccountConnectionCommand of(CharacterId characterId) {
        return new CancelAccountConnectionCommand(characterId);
    }
}