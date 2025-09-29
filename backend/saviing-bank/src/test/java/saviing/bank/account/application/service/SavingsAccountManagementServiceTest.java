package saviing.bank.account.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import saviing.bank.account.application.port.in.command.CloseSavingsAccountCommand;
import saviing.bank.account.application.port.in.command.UpdateSavingsAccountCommand;
import saviing.bank.account.application.port.in.result.GetAccountResult;
import saviing.bank.account.application.port.out.LoadAccountPort;
import saviing.bank.account.application.port.out.SaveAccountPort;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.AccountStatus;
import saviing.bank.account.domain.model.CompoundingType;
import saviing.bank.account.domain.model.Product;
import saviing.bank.account.domain.model.ProductCategory;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.AccountNumber;
import saviing.bank.account.domain.vo.BasisPoints;
import saviing.bank.account.domain.vo.InterestRateRange;
import saviing.bank.account.domain.vo.PaymentAmount;
import saviing.bank.account.domain.vo.ProductConfiguration;
import saviing.bank.account.domain.vo.ProductId;
import saviing.bank.account.domain.vo.TermPeriod;
import saviing.bank.account.exception.InvalidTargetAmountException;
import saviing.bank.account.exception.InvalidWithdrawalAccountException;
import saviing.bank.common.vo.MoneyWon;

@ExtendWith(MockitoExtension.class)
class SavingsAccountManagementServiceTest {

    @Mock
    private LoadAccountPort loadAccountPort;

    @Mock
    private SaveAccountPort saveAccountPort;

    @Mock
    private ProductService productService;

    @InjectMocks
    private SavingsAccountManagementService service;

    @Test
    void updateSavingsAccount_shouldUpdateTargetAndWithdrawalAccount() {
        Long accountId = 100L;
        Account savingsAccount = createSavingsAccount(accountId, "12345678901234", "55555555555555", 1_000_000L, 500_000L, ProductId.of(2L));
        Account withdrawalAccount = createDemandDepositAccount(200L, "99999999999999", savingsAccount.getCustomerId());

        when(loadAccountPort.findById(AccountId.of(accountId))).thenReturn(Optional.of(savingsAccount));
        when(loadAccountPort.findByAccountNumber(new AccountNumber("99999999999999")))
            .thenReturn(Optional.of(withdrawalAccount));
        when(productService.getProduct(ProductId.of(2L))).thenReturn(createSavingsProduct());
        when(productService.getProduct(ProductId.of(1L))).thenReturn(createDemandDepositProduct());
        when(saveAccountPort.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateSavingsAccountCommand command = UpdateSavingsAccountCommand.of(accountId, 1_500_000L, "99999999999999");

        GetAccountResult result = service.updateSavingsAccount(command);

        assertThat(result.savings().targetAmount()).isEqualTo(1_500_000L);
        assertThat(result.savings().maturityWithdrawalAccount()).isEqualTo("99999999999999");
    }

    @Test
    void updateSavingsAccount_shouldThrowWhenTargetAmountBelowBalance() {
        Long accountId = 100L;
        Account savingsAccount = createSavingsAccount(accountId, "12345678901234", "55555555555555", 1_000_000L, 500_000L, ProductId.of(2L));

        when(loadAccountPort.findById(AccountId.of(accountId))).thenReturn(Optional.of(savingsAccount));
        when(productService.getProduct(ProductId.of(2L))).thenReturn(createSavingsProduct());

        UpdateSavingsAccountCommand command = UpdateSavingsAccountCommand.of(accountId, 400_000L, null);

        assertThatThrownBy(() -> service.updateSavingsAccount(command))
            .isInstanceOf(InvalidTargetAmountException.class);
    }

    @Test
    void updateSavingsAccount_shouldThrowWhenWithdrawalAccountIsNotDemandDeposit() {
        Long accountId = 100L;
        Account savingsAccount = createSavingsAccount(accountId, "12345678901234", "55555555555555", 1_000_000L, 500_000L, ProductId.of(2L));
        Account invalidWithdrawalAccount = createSavingsAccount(300L, "88888888888888", null, 1_000_000L, 0L, ProductId.of(3L));

        when(loadAccountPort.findById(AccountId.of(accountId))).thenReturn(Optional.of(savingsAccount));
        when(loadAccountPort.findByAccountNumber(new AccountNumber("88888888888888")))
            .thenReturn(Optional.of(invalidWithdrawalAccount));
        when(productService.getProduct(ProductId.of(2L))).thenReturn(createSavingsProduct());
        when(productService.getProduct(ProductId.of(3L))).thenReturn(createOtherSavingsProduct());

        UpdateSavingsAccountCommand command = UpdateSavingsAccountCommand.of(accountId, 1_200_000L, "88888888888888");

        assertThatThrownBy(() -> service.updateSavingsAccount(command))
            .isInstanceOf(InvalidWithdrawalAccountException.class);
    }

    @Test
    void closeSavingsAccount_shouldChangeStatusToClosed() {
        Long accountId = 100L;
        Account savingsAccount = createSavingsAccount(accountId, "12345678901234", "55555555555555", 1_000_000L, 500_000L, ProductId.of(2L));

        when(loadAccountPort.findById(AccountId.of(accountId))).thenReturn(Optional.of(savingsAccount));
        when(productService.getProduct(ProductId.of(2L))).thenReturn(createSavingsProduct());
        when(saveAccountPort.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GetAccountResult result = service.closeSavingsAccount(CloseSavingsAccountCommand.of(accountId));

        assertThat(result.status()).isEqualTo(AccountStatus.CLOSED.name());
    }

    private Account createSavingsAccount(
        Long accountId,
        String accountNumber,
        String withdrawalAccount,
        long targetAmount,
        long balance,
        ProductId productId
    ) {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        return Account.restore(
            AccountId.of(accountId),
            new AccountNumber(accountNumber),
            1000L,
            productId,
            CompoundingType.DAILY,
            withdrawalAccount != null ? new AccountNumber(withdrawalAccount) : null,
            MoneyWon.of(targetAmount),
            TermPeriod.weeks(10),
            LocalDate.parse("2024-03-11"),
            AccountStatus.ACTIVE,
            now,
            null,
            now,
            now,
            now,
            now,
            MoneyWon.of(balance),
            BigDecimal.ZERO,
            BasisPoints.of(250),
            BasisPoints.zero()
        );
    }

    private Account createDemandDepositAccount(Long accountId, String accountNumber, Long customerId) {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        return Account.restore(
            AccountId.of(accountId),
            new AccountNumber(accountNumber),
            customerId,
            ProductId.of(1L),
            CompoundingType.DAILY,
            null,
            null,
            null,
            null,
            AccountStatus.ACTIVE,
            now,
            null,
            now,
            now,
            now,
            now,
            MoneyWon.of(100_000L),
            BigDecimal.ZERO,
            BasisPoints.zero(),
            BasisPoints.zero()
        );
    }

    private Product createSavingsProduct() {
        ProductConfiguration configuration = ProductConfiguration.builder()
            .category(ProductCategory.INSTALLMENT_SAVINGS)
            .interestRateRange(InterestRateRange.of(BasisPoints.of(200), BasisPoints.of(450)))
            .paymentAmount(PaymentAmount.of(100_000L, 2_000_000L))
            .build();
        return Product.of(
            ProductId.of(2L),
            ProductCategory.INSTALLMENT_SAVINGS,
            "자유적금",
            "FREE_SAVINGS",
            configuration
        );
    }

    private Product createDemandDepositProduct() {
        ProductConfiguration configuration = ProductConfiguration.builder()
            .category(ProductCategory.DEMAND_DEPOSIT)
            .interestRateRange(InterestRateRange.zero())
            .build();
        return Product.of(
            ProductId.of(1L),
            ProductCategory.DEMAND_DEPOSIT,
            "자유입출금",
            "FREE_CHECKING",
            configuration
        );
    }

    private Product createOtherSavingsProduct() {
        ProductConfiguration configuration = ProductConfiguration.builder()
            .category(ProductCategory.INSTALLMENT_SAVINGS)
            .interestRateRange(InterestRateRange.of(BasisPoints.of(200), BasisPoints.of(450)))
            .paymentAmount(PaymentAmount.of(100_000L, 2_000_000L))
            .build();
        return Product.of(
            ProductId.of(3L),
            ProductCategory.INSTALLMENT_SAVINGS,
            "다른적금",
            "ALT_SAVINGS",
            configuration
        );
    }
}
