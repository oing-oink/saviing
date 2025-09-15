package saviing.game.character.application.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import saviing.game.character.domain.event.DomainEvent;

import java.util.List;

/**
 * Spring의 ApplicationEventPublisher를 활용한 도메인 이벤트 발행 구현체
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    @Override
    public void publish(DomainEvent event) {
        log.debug("Publishing domain event: {}", event.eventType());
        applicationEventPublisher.publishEvent(event);
    }
    
    @Override
    public void publishAll(List<DomainEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        
        log.debug("Publishing {} domain events", events.size());
        events.forEach(this::publish);
    }
}