package saviing.common.aspect;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import lombok.extern.slf4j.Slf4j;
import saviing.common.annotation.ExecutionTime;
import saviing.common.annotation.LogLevel;

@Aspect
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExecutionTimeAspect {

    private static final String INDENT = "──";
    
    private static final ThreadLocal<Integer> EXECUTION_DEPTH = ThreadLocal.withInitial(() -> 0);
    
    @Pointcut("@annotation(saviing.common.annotation.ExecutionTime)")
    public void annotatedMethod() {}
    
    @Pointcut("@within(saviing.common.annotation.ExecutionTime)")
    public void annotatedClass() {}
    
    @Around("execution(* *(..)) && !execution(static * *..*(..)) && (annotatedMethod() || annotatedClass())")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        ExecutionTime executionTime = getExecutionTimeAnnotation(method, joinPoint.getTarget().getClass());
        if (executionTime == null) {
            return joinPoint.proceed();
        }
        
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        
        int currentDepth = EXECUTION_DEPTH.get();
        EXECUTION_DEPTH.set(currentDepth + 1);
        
        String indent = INDENT.repeat(currentDepth);
        
        long startTime = System.nanoTime();
        
        logMessage(String.format("%s %s.%s() 메서드 시작", indent, className, methodName), executionTime.level());
        
        try {
            Object result = joinPoint.proceed();
            return result;
        } finally {
            long endTime = System.nanoTime();
            long executionTimeMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            
            EXECUTION_DEPTH.set(currentDepth);
            if (currentDepth == 0) {
                EXECUTION_DEPTH.remove();
            }
            
            logExecutionTime(className, methodName, executionTimeMs, executionTime);
        }
    }
    
    private ExecutionTime getExecutionTimeAnnotation(Method method, Class<?> targetClass) {
        ExecutionTime annotation = method.getAnnotation(ExecutionTime.class);
        if (annotation != null) {
            return annotation;
        }
        
        return targetClass.getAnnotation(ExecutionTime.class);
    }
    
    private void logExecutionTime(String className, String methodName, 
            long executionTimeMs, ExecutionTime executionTime) {

        int currentDepth = EXECUTION_DEPTH.get();
        String indent = INDENT.repeat(currentDepth);
        String message = String.format("%s %s.%s() 메서드 종료. [실행 시간: %d ms]",  indent, className, methodName, executionTimeMs);
        
        LogLevel logLevel = executionTime.level();
        boolean isOverThreshold = executionTimeMs >= executionTime.threshold();
        
        if (isOverThreshold) {
            log.warn("{} (임계값 초과: {} ms)", message, executionTime.threshold());
        } else {
            logMessage(message, logLevel);
        }
    }
    
    private void logMessage(String message, LogLevel logLevel) {
        switch (logLevel) {
            case DEBUG -> log.debug(message);
            case INFO -> log.info(message);
            case WARN -> log.warn(message);
            case ERROR -> log.error(message);
            default -> log.info(message);
        }
    }
}
