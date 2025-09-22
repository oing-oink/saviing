package saviing.bank.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import saviing.bank.common.vo.MoneyWon;
import saviing.common.annotation.ExecutionTime;
import saviing.bank.account.application.port.in.CreateAccountUseCase;
import saviing.bank.account.application.port.in.command.CreateAccountCommand;
import saviing.bank.account.application.port.in.command.CreateDemandDepositCommand;
import saviing.bank.account.application.port.in.command.CreateSavingsCommand;
import saviing.bank.account.application.port.in.result.CreateAccountResult;
import saviing.bank.account.application.port.out.AutoTransferSchedulePort;
import saviing.bank.account.application.port.out.GenerateAccountNumberPort;
import saviing.bank.account.application.port.out.LoadAccountPort;
import saviing.bank.account.application.port.out.SaveAccountPort;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.AccountStatus;
import saviing.bank.account.domain.model.AutoTransferSchedule;
import saviing.bank.account.domain.model.Product;
import saviing.bank.account.domain.model.ProductCategory;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.AccountNumber;
import saviing.bank.account.domain.vo.BasisPoints;
import saviing.bank.account.domain.vo.InterestRateRange;
import saviing.bank.account.domain.vo.ProductConfiguration;
import saviing.bank.account.exception.InvalidProductTypeException;
import saviing.bank.account.exception.InvalidSavingsTermException;
import saviing.bank.account.exception.InvalidTargetAmountException;
import saviing.bank.account.exception.InvalidWithdrawalAccountException;

@ExecutionTime
@Service
@Transactional
@RequiredArgsConstructor
public class AccountCommandService implements CreateAccountUseCase {

    private final GenerateAccountNumberPort generateAccountNumberPort;
    private final SaveAccountPort saveAccountPort;
    private final LoadAccountPort loadAccountPort;
    private final ProductService productService;
    private final AutoTransferSchedulePort autoTransferSchedulePort;
    
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

        // 자유입출금 계좌 1000만원 지급 (테스트)
        account.deposit(MoneyWon.of(10_000_000L));

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
        AutoTransferSchedule schedule = initializeAutoTransferSchedule(command, savedAccount);
        return CreateAccountResult.from(savedAccount, product, schedule);
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
     * @throws InvalidProductTypeException Command 타입과 상품 타입이 일치하지 않는 경우
     */
    private void validateCommandProductTypeMatch(CreateAccountCommand command, Product product) {
        switch (command) {
            case CreateDemandDepositCommand demandDeposit -> {
                if (product.getCategory() != ProductCategory.DEMAND_DEPOSIT) {
                    throw new InvalidProductTypeException(Map.of(
                        "requestedType", "DEMAND_DEPOSIT",
                        "actualType", product.getCategory(),
                        "productId", product.getId()
                    ));
                }
            }
            case CreateSavingsCommand savings -> {
                if (product.getCategory() != ProductCategory.INSTALLMENT_SAVINGS) {
                    throw new InvalidProductTypeException(Map.of(
                        "requestedType", "INSTALLMENT_SAVINGS",
                        "actualType", product.getCategory(),
                        "productId", product.getId()
                    ));
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
     * @throws InvalidSavingsTermException 기간이 상품 제약에 맞지 않는 경우
     * @throws InvalidTargetAmountException 목표금액이 유효하지 않은 경우
     */
    private void validateSavingsCreation(CreateSavingsCommand command, Product product) {
        // 상품 설정과 기간 일치성 검증
        ProductConfiguration config = product.getConfiguration();
        if (config != null && !config.isValidTerm(command.termPeriod())) {
            throw new InvalidSavingsTermException(Map.of(
                "termPeriod", command.termPeriod(),
                "productId", product.getId(),
                "validTerms", config.getTermConstraints()
            ));
        }

        if (!command.targetAmount().isPositive()) {
            throw new InvalidTargetAmountException(Map.of(
                "targetAmount", command.targetAmount(),
                "customerId", command.customerId()
            ));
        }
    }

    /**
     * 적금 계좌 개설 시 초기 자동이체 설정을 저장한다.
     *
     * @param command 적금 계좌 생성 명령
     * @param savedAccount 자동이체를 연결할 저장된 계좌
     */
    private AutoTransferSchedule initializeAutoTransferSchedule(CreateSavingsCommand command, Account savedAccount) {
        if (command.autoTransfer() == null || !command.autoTransfer().enabled()) {
            return null;
        }

        Account withdrawAccount = resolveAutoTransferWithdrawAccount(command.autoTransfer().withdrawAccountId(), savedAccount);

        AutoTransferSchedule schedule = AutoTransferSchedule.create(
            savedAccount.getId(),
            command.autoTransfer().cycle(),
            withdrawAccount.getId(),
            command.autoTransfer().transferDay(),
            command.autoTransfer().amount(),
            true,
            LocalDate.now(),
            Instant.now()
        );
        autoTransferSchedulePort.create(schedule);
        return schedule;
    }

    /**
     * 자동이체 출금 계좌를 검증한다
     * 
     * @param withdrawAccountId 출금 계좌 ID
     * @param savingsAccount 적금 계좌
     * @return 출금 계좌
     */
    private Account resolveAutoTransferWithdrawAccount(Long withdrawAccountId, Account savingsAccount) {
        if (withdrawAccountId == null) {
            throw new InvalidWithdrawalAccountException(Map.of(
                "reason", "WITHDRAW_ACCOUNT_REQUIRED"
            ));
        }

        Account withdrawAccount = loadAccountPort.findById(AccountId.of(withdrawAccountId))
            .orElseThrow(() -> new InvalidWithdrawalAccountException(Map.of(
                "withdrawAccountId", withdrawAccountId,
                "reason", "NOT_FOUND"
            )));

        if (!withdrawAccount.getCustomerId().equals(savingsAccount.getCustomerId())) {
            throw new InvalidWithdrawalAccountException(Map.of(
                "withdrawAccountId", withdrawAccountId,
                "reason", "DIFFERENT_CUSTOMER"
            ));
        }

        if (withdrawAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidWithdrawalAccountException(Map.of(
                "withdrawAccountId", withdrawAccountId,
                "reason", "ACCOUNT_INACTIVE"
            ));
        }

        Product withdrawProduct = productService.getProduct(withdrawAccount.getProductId());
        if (withdrawProduct.getCategory() != ProductCategory.DEMAND_DEPOSIT) {
            throw new InvalidWithdrawalAccountException(Map.of(
                "withdrawAccountId", withdrawAccountId,
                "reason", "NOT_DEMAND_DEPOSIT"
            ));
        }

        return withdrawAccount;
    }
}
