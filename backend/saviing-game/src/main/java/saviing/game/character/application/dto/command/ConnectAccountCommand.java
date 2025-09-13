package saviing.game.character.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;

/**
 * 계좌 연결 시작 Command입니다.
 *
 * @param characterId 캐릭터 ID
 * @param accountId 계좌 ID
 */
@Builder
public record ConnectAccountCommand(
    CharacterId characterId,
    Long accountId
) {
    public ConnectAccountCommand {
        if (characterId == null) {
            throw new IllegalArgumentException("캐릭터 ID는 필수입니다");
        }
        if (accountId == null) {
            throw new IllegalArgumentException("계좌 ID는 필수입니다");
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
     * @return ConnectAccountCommand 인스턴스
     */
    public static ConnectAccountCommand of(CharacterId characterId, Long accountId) {
        return ConnectAccountCommand.builder()
            .characterId(characterId)
            .accountId(accountId)
            .build();
    }
}