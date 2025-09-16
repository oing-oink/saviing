package saviing.bank.account.adapter.out.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Random;

import saviing.common.annotation.ExecutionTime;
import saviing.bank.account.application.port.out.GenerateAccountNumberPort;
import saviing.bank.account.application.port.out.LoadAccountPort;
import saviing.bank.account.domain.vo.AccountNumber;

@ExecutionTime
@Component
@RequiredArgsConstructor
public class DefaultAccountNumberGenerator implements GenerateAccountNumberPort {
    
    private final LoadAccountPort loadAccountPort;
    private final Random random = new Random();
    
    @Override
    public AccountNumber generateUniqueAccountNumber() {
        AccountNumber accountNumber;
        int attempts = 0;
        int maxAttempts = 10;
        
        do {
            accountNumber = generateAccountNumber();
            attempts++;
        } while (loadAccountPort.existsByAccountNumber(accountNumber) && attempts < maxAttempts);
        
        if (attempts >= maxAttempts) {
            throw new RuntimeException("계좌번호 생성에 실패했습니다");
        }
        
        return accountNumber;
    }
    
    private AccountNumber generateAccountNumber() {
        // 간단한 계좌번호 생성 로직 (실제로는 더 복잡한 규칙이 필요할 수 있음)
        long timestamp = Instant.now().toEpochMilli();
        int randomPart = random.nextInt(9999);
        
        String accountNumberStr = String.format("%010d%04d", timestamp % 1000000000L, randomPart);
        
        return new AccountNumber(accountNumberStr);
    }
}