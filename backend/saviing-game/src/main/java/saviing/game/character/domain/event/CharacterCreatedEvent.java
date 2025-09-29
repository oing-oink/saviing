package saviing.game.character.domain.event;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.model.vo.CustomerId;

import java.time.LocalDateTime;

/**
 * 캐릭터가 생성되었을 때 발행되는 도메인 이벤트
 */
public record CharacterCreatedEvent(
    CharacterId characterId,
    CustomerId customerId,
    LocalDateTime occurredOn
) implements CharacterDomainEvent {
    
    /**
     * 캐릭터 생성 이벤트를 생성합니다.
     * 
     * @param characterId 생성된 캐릭터 ID
     * @param customerId 고객 ID
     * @return CharacterCreatedEvent
     */
    public static CharacterCreatedEvent of(CharacterId characterId, CustomerId customerId) {
        return new CharacterCreatedEvent(characterId, customerId, LocalDateTime.now());
    }
}