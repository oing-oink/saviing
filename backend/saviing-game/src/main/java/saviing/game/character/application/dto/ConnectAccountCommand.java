package saviing.game.character.application.dto;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;

/**
 * 계좌 연결 시작 Command
 */
@Builder
public record ConnectAccountCommand(
    CharacterId characterId,
    Long accountId
) {
    public ConnectAccountCommand {
        if (accountId == null) {
            throw new IllegalArgumentException("계좌 ID는 null일 수 없습니다");
        }
        if (accountId <= 0) {
            throw new IllegalArgumentException("계좌 ID는 양수여야 합니다");
        }
    }
    
    /**
     * ConnectAccountCommand를 생성합니다.
     * 
     * @param characterId 캐릭터 ID
     * @param accountId 계좌 ID
     * @return ConnectAccountCommand
     */
    public static ConnectAccountCommand of(CharacterId characterId, Long accountId) {
        return ConnectAccountCommand.builder()
            .characterId(characterId)
            .accountId(accountId)
            .build();
    }
}