package saviing.game.character.domain.event;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.model.vo.CustomerId;

import java.time.LocalDateTime;

/**
 * 계좌 해지가 처리되었을 때 발행되는 도메인 이벤트
 */
public record AccountTerminatedEvent(
    CharacterId characterId,
    CustomerId customerId,
    String reason,
    LocalDateTime occurredOn
) implements CharacterDomainEvent {
    
    /**
     * 계좌 해지 처리 이벤트를 생성합니다.
     * 
     * @param characterId 캐릭터 ID
     * @param customerId 고객 ID
     * @param reason 해지 사유
     * @return AccountTerminatedEvent
     */
    public static AccountTerminatedEvent of(CharacterId characterId, CustomerId customerId, String reason) {
        return new AccountTerminatedEvent(characterId, customerId, reason, LocalDateTime.now());
    }
}