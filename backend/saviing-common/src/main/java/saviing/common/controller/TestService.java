package saviing.common.controller;

import saviing.common.annotation.ExecutionTime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@ExecutionTime
@Slf4j
@Service
public class TestService {
    
    public String processData(String input) throws InterruptedException {
        Thread.sleep(50); // 50ms 지연
        return "Processed: " + input;
    }
    
    public void performCalculation(int value) throws InterruptedException {
        Thread.sleep(30); // 30ms 지연
        
        // 다른 서비스 메서드 호출
        validateInput(value);
    }
    
    public void validateInput(int value) throws InterruptedException {
        Thread.sleep(20); // 20ms 지연
        
        if (value < 0) {
            throw new IllegalArgumentException("값이 음수입니다: " + value);
        }
    }
    
    public String fetchExternalData() throws InterruptedException {
        Thread.sleep(100); // 100ms 지연 (외부 API 호출 시뮬레이션)
        return "External data response";
    }
    
    // 깊이 3단계까지의 메서드 호출 체인
    public String deepProcessing(String input) throws InterruptedException {
        Thread.sleep(30); // Level 1 지연
        log.info("Level 1: Deep processing started for: {}", input);
        
        // Level 2 호출
        String intermediate = levelTwoProcessing(input);
        return "Deep result: " + intermediate;
    }
    
    public String levelTwoProcessing(String input) throws InterruptedException {
        Thread.sleep(40); // Level 2 지연
        log.info("Level 2: Intermediate processing for: {}", input);
        
        // Level 3 호출
        String validated = levelThreeValidation(input);
        return "Level2[" + validated + "]";
    }
    
    public String levelThreeValidation(String input) throws InterruptedException {
        Thread.sleep(25); // Level 3 지연
        log.info("Level 3: Final validation for: {}", input);
        
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        
        return "Validated[" + input.toUpperCase() + "]";
    }
}