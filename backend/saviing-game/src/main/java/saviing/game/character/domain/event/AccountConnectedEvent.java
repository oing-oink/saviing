package saviing.game.character.domain.event;

import java.time.LocalDateTime;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.model.vo.CustomerId;

/**
 * 계좌 연결이 완료되었을 때 발행되는 도메인 이벤트
 */
public record AccountConnectedEvent(
    CharacterId characterId,
    CustomerId customerId,
    Long accountId,
    LocalDateTime occurredOn
) implements CharacterDomainEvent {
    /**
     * 계좌 연결 완료 이벤트를 생성합니다.
     * 
     * @param characterId 캐릭터 ID
     * @param customerId 고객 ID
     * @param accountId 연결된 계좌 ID
     * @return AccountConnectedEvent
     */
    public static AccountConnectedEvent of(CharacterId characterId, CustomerId customerId, Long accountId) {
        return new AccountConnectedEvent(characterId, customerId, accountId, LocalDateTime.now());
    }
}