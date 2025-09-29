package saviing.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnotherTestService {
    
    public String formatResult(String data) throws InterruptedException {
        Thread.sleep(25); // 25ms 지연
        return "[FORMATTED] " + data;
    }
    
    public void saveToDatabase(String data) throws InterruptedException {
        Thread.sleep(80); // 80ms 지연 (DB 저장 시뮬레이션)
    }
}