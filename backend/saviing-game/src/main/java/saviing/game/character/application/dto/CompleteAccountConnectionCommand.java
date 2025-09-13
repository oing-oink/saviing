package saviing.game.character.application.dto;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;

/**
 * 계좌 연결 완료 Command
 */
@Builder
public record CompleteAccountConnectionCommand(
    CharacterId characterId,
    Long accountId
) {
    public CompleteAccountConnectionCommand {
        if (accountId == null) {
            throw new IllegalArgumentException("계좌 ID는 null일 수 없습니다");
        }
        if (accountId <= 0) {
            throw new IllegalArgumentException("계좌 ID는 양수여야 합니다");
        }
    }
    
    /**
     * CompleteAccountConnectionCommand를 생성합니다.
     * 
     * @param characterId 캐릭터 ID
     * @param accountId 연결 완료된 계좌 ID
     * @return CompleteAccountConnectionCommand
     */
    public static CompleteAccountConnectionCommand of(CharacterId characterId, Long accountId) {
        return CompleteAccountConnectionCommand.builder()
            .characterId(characterId)
            .accountId(accountId)
            .build();
    }
}