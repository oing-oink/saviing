package saviing.bank.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import saviing.bank.account.application.port.in.CreateAccountUseCase;
import saviing.bank.account.application.port.in.command.CreateAccountCommand;
import saviing.bank.account.application.port.in.command.CreateDemandDepositCommand;
import saviing.bank.account.application.port.in.command.CreateSavingsCommand;
import saviing.bank.account.application.port.in.result.CreateAccountResult;
import saviing.bank.account.application.port.out.GenerateAccountNumberPort;
import saviing.bank.account.application.port.out.SaveAccountPort;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.Product;
import saviing.bank.account.domain.model.ProductCategory;
import saviing.bank.account.domain.vo.AccountNumber;
import saviing.bank.account.domain.vo.BasisPoints;
import saviing.bank.account.domain.vo.InterestRateRange;
import saviing.bank.account.domain.vo.ProductConfiguration;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements CreateAccountUseCase {

    private final GenerateAccountNumberPort generateAccountNumberPort;
    private final SaveAccountPort saveAccountPort;
    private final ProductService productService;
    
    /**
     * 계좌 생성 요청을 처리합니다.
     *
     * Pattern Matching을 통해 상품 카테고리에 따른 분기 처리를 합니다.
     *
     * @param command 계좌 생성 명령 (CreateDemandDepositCommand 또는 CreateSavingsCommand)
     * @return 생성된 계좌 정보
     * @throws IllegalArgumentException 상품이 존재하지 않거나, Command와 상품 타입이 일치하지 않는 경우
     */
    @Override
    public CreateAccountResult createAccount(CreateAccountCommand command) {
        // 1. 상품 조회 및 존재성 검증
        Product product = productService.getProduct(command.productId());

        // 2. Command 타입과 상품 타입 일치성 검증
        validateCommandProductTypeMatch(command, product);

        return switch (command) {
            case CreateDemandDepositCommand demandDeposit -> createDemandDepositAccount(demandDeposit, product);
            case CreateSavingsCommand savings -> createSavingsAccount(savings, product);
        };
    }

    private CreateAccountResult createDemandDepositAccount(CreateDemandDepositCommand command, Product product) {
        // 추가적인 자유입출금 계좌 검증 (필요시)
        validateDemandDepositCreation(command, product);

        AccountNumber accountNumber = generateAccountNumberPort.generateUniqueAccountNumber();

        Account account = Account.open(
            accountNumber,
            command.customerId(),
            command.productId(),
            Instant.now()
        );

        // 상품별 기본 금리 설정 (자유입출금은 0%)
        setDefaultInterestRate(account, product);

        Account savedAccount = saveAccountPort.save(account);
        return CreateAccountResult.from(savedAccount, product);
    }

    private CreateAccountResult createSavingsAccount(CreateSavingsCommand command, Product product) {
        // 적금 계좌 상세 검증
        validateSavingsCreation(command, product);

        AccountNumber accountNumber = generateAccountNumberPort.generateUniqueAccountNumber();

        Account account = Account.open(
            accountNumber,
            command.customerId(),
            command.productId(),
            Instant.now()
        );

        // 적금 설정 추가
        account.setSavingsSettings(
            command.targetAmount(),
            command.termPeriod(),
            command.maturityWithdrawalAccount(),
            Instant.now()
        );

        // 상품별 기본 금리 설정
        setDefaultInterestRate(account, product);

        Account savedAccount = saveAccountPort.save(account);
        return CreateAccountResult.from(savedAccount, product);
    }

    private void setDefaultInterestRate(Account account, Product product) {
        ProductConfiguration config = product.getConfiguration();
        if (config != null && config.getInterestRateRange() != null) {
            InterestRateRange rateRange = config.getInterestRateRange();
            // 기본금리는 최소 이자율로, 보너스금리는 0%로 설정
            account.changeRates(
                rateRange.minRate(), // 기본금리 (최소값)
                BasisPoints.zero(), // 보너스금리 0%
                Instant.now()
            );
        }
        // 금리 범위가 설정되지 않은 상품은 기본값(0%) 유지
    }

    /**
     * Command 타입과 상품 타입의 일치성을 검증합니다.
     *
     * 잘못된 Command-Product 조합을 방지합니다.
     * 예: CreateSavingsCommand인데 자유입출금 상품을 요청하는 경우
     *
     * @param command 계좌 생성 명령
     * @param product 조회된 상품 정보
     * @throws IllegalArgumentException Command 타입과 상품 타입이 일치하지 않는 경우
     */
    private void validateCommandProductTypeMatch(CreateAccountCommand command, Product product) {
        switch (command) {
            case CreateDemandDepositCommand demandDeposit -> {
                if (product.getCategory() != ProductCategory.DEMAND_DEPOSIT) {
                    throw new IllegalArgumentException(
                        "자유입출금 계좌 생성 요청이지만 상품이 자유입출금이 아닙니다. 상품타입: " + product.getCategory()
                    );
                }
            }
            case CreateSavingsCommand savings -> {
                if (product.getCategory() != ProductCategory.INSTALLMENT_SAVINGS) {
                    throw new IllegalArgumentException(
                        "적금 계좌 생성 요청이지만 상품이 적금이 아닙니다. 상품타입: " + product.getCategory()
                    );
                }
            }
        }
    }

    /**
     * 자유입출금 계좌 생성을 위한 상세 검증을 수행합니다.
     *
     * 현재는 특별한 검증 로직이 없지만, 향후 최소잔액, 수수료 등의
     * 자유입출금 계좌만의 비즈니스 규칙을 추가할 수 있습니다.
     *
     * @param command 자유입출금 계좌 생성 명령
     * @param product 상품 정보
     */
    private void validateDemandDepositCreation(CreateDemandDepositCommand command, Product product) {
        // 자유입출금 계좌의 추가 검증 로직 (현재는 특별한 검증 없음)
        // 향후 필요시 추가 (예: 최소잔액, 수수료 등)
    }

    /**
     * 적금 계좌 생성을 위한 상세 검증을 수행합니다.
     *
     * 상품별 기간 제약 조건과 목표금액의 유효성을 검증합니다.
     * 실제 은행과 같은 세밀한 비즈니스 규칙을 적용합니다.
     *
     * @param command 적금 계좌 생성 명령
     * @param product 상품 정보
     * @throws IllegalArgumentException 기간이 상품 제약에 맞지 않거나 목표금액이 유효하지 않은 경우
     */
    private void validateSavingsCreation(CreateSavingsCommand command, Product product) {
        // 상품 설정과 기간 일치성 검증
        ProductConfiguration config = product.getConfiguration();
        if (config != null && !config.isValidTerm(command.termPeriod())) {
            throw new IllegalArgumentException("유효하지 않은 적금 기간입니다: " + command.termPeriod());
        }

        if (!command.targetAmount().isPositive()) {
            throw new IllegalArgumentException("목표금액은 0보다 커야 합니다");
        }
    }
}