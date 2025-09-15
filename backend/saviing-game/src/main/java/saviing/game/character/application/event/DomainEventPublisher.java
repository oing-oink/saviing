package saviing.game.character.application.event;

import saviing.game.character.domain.event.DomainEvent;

import java.util.List;

/**
 * 도메인 이벤트 발행을 담당하는 인터페이스
 */
public interface DomainEventPublisher {
    
    /**
     * 단일 도메인 이벤트를 발행합니다.
     * 
     * @param event 발행할 도메인 이벤트
     */
    void publish(DomainEvent event);
    
    /**
     * 여러 도메인 이벤트를 일괄 발행합니다.
     * 
     * @param events 발행할 도메인 이벤트 목록
     */
    void publishAll(List<DomainEvent> events);
}