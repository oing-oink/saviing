package saviing.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메소드 실행 시간을 측정하고 로깅하는 어노테이션
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutionTime {
    
    /**
     * 로그 레벨 (기본: DEBUG)
     */
    LogLevel level() default LogLevel.DEBUG;
    
    /**
     * 임계값 (ms) - 이 시간 이상 걸리면 경고 로그 출력 (기본: 1000ms)
     */
    long threshold() default 1000L;
}