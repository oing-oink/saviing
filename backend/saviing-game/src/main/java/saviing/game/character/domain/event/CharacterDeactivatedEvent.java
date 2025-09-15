package saviing.game.character.domain.event;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.model.vo.CustomerId;

import java.time.LocalDateTime;

/**
 * 캐릭터가 비활성화되었을 때 발행되는 도메인 이벤트
 */
public record CharacterDeactivatedEvent(
    CharacterId characterId,
    CustomerId customerId,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    /**
     * 캐릭터 비활성화 이벤트를 생성합니다.
     * 
     * @param characterId 비활성화된 캐릭터 ID
     * @param customerId 고객 ID
     * @return CharacterDeactivatedEvent
     */
    public static CharacterDeactivatedEvent of(CharacterId characterId, CustomerId customerId) {
        return new CharacterDeactivatedEvent(characterId, customerId, LocalDateTime.now());
    }
}