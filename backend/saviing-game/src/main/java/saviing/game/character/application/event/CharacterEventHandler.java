package saviing.game.character.application.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import saviing.game.character.domain.event.AccountConnectedEvent;
import saviing.game.character.domain.event.AccountTerminatedEvent;
import saviing.game.character.domain.event.CharacterCreatedEvent;
import saviing.game.character.domain.event.CharacterDeactivatedEvent;

/**
 * 캐릭터 도메인 이벤트 핸들러
 * 도메인 이벤트를 수신하고 후속 작업을 처리합니다.
 */
@Slf4j
@Component
public class CharacterEventHandler {
    
    /**
     * 캐릭터 생성 이벤트를 처리합니다.
     * 
     * @param event 캐릭터 생성 이벤트
     */
    @EventListener
    public void handleCharacterCreated(CharacterCreatedEvent event) {
        log.info("Character created event received - Character: {}, Customer: {}", 
                event.characterId() != null ? event.characterId().value() : "null",
                event.customerId().value());
        
        // TODO: 필요한 후속 작업 처리
        // 예: 통계 업데이트, 알림 발송, 외부 시스템 연동 등
    }
    
    /**
     * 계좌 연결 완료 이벤트를 처리합니다.
     * 
     * @param event 계좌 연결 완료 이벤트
     */
    @EventListener
    public void handleAccountConnected(AccountConnectedEvent event) {
        log.info("Account connected event received - Character: {}, Customer: {}, Account: {}", 
                event.characterId().value(), 
                event.customerId().value(), 
                event.accountId());
        
        // TODO: 필요한 후속 작업 처리
        // 예: 계좌 연결 확인 알림, 보안 로그 기록 등
    }
    
    /**
     * 계좌 해지 이벤트를 처리합니다.
     * 
     * @param event 계좌 해지 이벤트
     */
    @EventListener
    public void handleAccountTerminated(AccountTerminatedEvent event) {
        log.info("Account terminated event received - Character: {}, Customer: {}, Reason: {}", 
                event.characterId().value(), 
                event.customerId().value(), 
                event.reason());
        
        // TODO: 필요한 후속 작업 처리
        // 예: 해지 처리 알림, 데이터 정리 작업 등
    }
    
    /**
     * 캐릭터 비활성화 이벤트를 처리합니다.
     * 
     * @param event 캐릭터 비활성화 이벤트
     */
    @EventListener
    public void handleCharacterDeactivated(CharacterDeactivatedEvent event) {
        log.info("Character deactivated event received - Character: {}, Customer: {}", 
                event.characterId().value(), 
                event.customerId().value());
        
        // TODO: 필요한 후속 작업 처리
        // 예: 비활성화 알림, 리소스 정리, 통계 업데이트 등
    }
}