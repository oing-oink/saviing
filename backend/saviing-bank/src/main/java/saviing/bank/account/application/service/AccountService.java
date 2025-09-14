package saviing.bank.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import saviing.bank.account.application.port.in.CreateAccountUseCase;
import saviing.bank.account.application.port.in.command.CreateAccountCommand;
import saviing.bank.account.application.port.in.result.CreateAccountResult;
import saviing.bank.account.application.port.out.GenerateAccountNumberPort;
import saviing.bank.account.application.port.out.SaveAccountPort;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.Product;
import saviing.bank.account.domain.model.ProductCategory;
import saviing.bank.account.domain.vo.AccountNumber;
import saviing.bank.account.domain.vo.BasisPoints;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements CreateAccountUseCase {

    private final GenerateAccountNumberPort generateAccountNumberPort;
    private final SaveAccountPort saveAccountPort;
    private final ProductService productService;
    
    @Override
    public CreateAccountResult createAccount(CreateAccountCommand command) {
        // 상품 존재 여부 검증 및 상품 정보 조회
        var product = productService.getProduct(command.productId());

        AccountNumber accountNumber = generateAccountNumberPort.generateUniqueAccountNumber();

        Account account = Account.open(
            accountNumber,
            command.customerId(),
            command.productId(),
            Instant.now()
        );

        // 상품별 기본 금리 설정
        setDefaultInterestRate(account, product);

        Account savedAccount = saveAccountPort.save(account);
        return CreateAccountResult.from(savedAccount, product);
    }

    private void setDefaultInterestRate(Account account, Product product) {
        var config = product.getConfiguration();
        if (config != null && config.getInterestRateRange() != null) {
            var rateRange = config.getInterestRateRange();
            // 기본금리는 최소 이자율로, 보너스금리는 0%로 설정
            account.changeRates(
                rateRange.minRate(), // 기본금리 (최소값)
                BasisPoints.zero(),     // 보너스금리 0%
                Instant.now()
            );
        }
        // 금리 범위가 설정되지 않은 상품은 기본값(0%) 유지
    }
}