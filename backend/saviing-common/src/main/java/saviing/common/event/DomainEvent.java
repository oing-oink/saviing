package saviing.common.event;

import java.time.LocalDateTime;

/**
 * 도메인 이벤트의 기본 인터페이스
 * 모든 도메인 이벤트가 구현해야 하는 공통 메서드를 정의합니다.
 */
public interface DomainEvent {

    /**
     * 이벤트가 발생한 시간을 반환합니다.
     *
     * @return 이벤트 발생 시간
     */
    LocalDateTime occurredOn();

    /**
     * 이벤트 타입을 반환합니다.
     *
     * @return 이벤트 타입 (클래스명)
     */
    default String eventType() {
        return this.getClass().getSimpleName();
    }
}