package saviing.bank.account.application.service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import saviing.bank.account.application.port.in.CloseSavingsAccountUseCase;
import saviing.bank.account.application.port.in.UpdateSavingsAccountUseCase;
import saviing.bank.account.application.port.in.UpdateAutoTransferScheduleUseCase;
import saviing.bank.account.application.port.in.command.CloseSavingsAccountCommand;
import saviing.bank.account.application.port.in.command.UpdateSavingsAccountCommand;
import saviing.bank.account.application.port.in.result.GetAccountResult;
import saviing.bank.account.application.port.out.LoadAccountPort;
import saviing.bank.account.application.port.out.SaveAccountPort;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.AccountStatus;
import saviing.bank.account.domain.model.Product;
import saviing.bank.account.domain.model.ProductCategory;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.AccountNumber;
import saviing.bank.account.exception.AccountNotFoundException;
import saviing.bank.account.exception.InvalidAccountStateException;
import saviing.bank.account.exception.InvalidProductTypeException;
import saviing.bank.account.exception.InvalidTargetAmountException;
import saviing.bank.account.exception.InvalidWithdrawalAccountException;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.account.application.port.in.command.UpdateAutoTransferScheduleCommand;
import saviing.bank.account.domain.model.AutoTransferSchedule;
import saviing.bank.account.domain.model.AutoTransferCycle;
import saviing.bank.account.application.port.out.AutoTransferSchedulePort;
import java.time.LocalDate;
import saviing.common.annotation.ExecutionTime;

/**
 * 적금 계좌의 설정 변경 및 해지를 담당하는 응용 서비스입니다.
 */
@ExecutionTime
@Service
@Transactional
@RequiredArgsConstructor
public class SavingsAccountManagementService implements UpdateSavingsAccountUseCase, CloseSavingsAccountUseCase, UpdateAutoTransferScheduleUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;
    private final ProductService productService;
    private final AutoTransferSchedulePort autoTransferSchedulePort;

    @Override
    public GetAccountResult updateSavingsAccount(UpdateSavingsAccountCommand command) {
        Long accountId = Optional.ofNullable(command.accountId())
            .orElseThrow(() -> new InvalidAccountStateException(Map.of("reason", "ACCOUNT_ID_REQUIRED")));

        Account account = loadAccountPort.findById(AccountId.of(accountId))
            .orElseThrow(() -> new AccountNotFoundException(Map.of("accountId", accountId)));

        Product product = validateSavingsAccount(account);

        MoneyWon targetAmount = resolveTargetAmount(command, account, product);
        AccountNumber maturityWithdrawalAccount = resolveWithdrawalAccount(command, account);

        account.updateSavingsSettings(targetAmount, maturityWithdrawalAccount, Instant.now());

        Account saved = saveAccountPort.save(account);
        return GetAccountResult.from(saved, product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GetAccountResult updateAutoTransferSchedule(UpdateAutoTransferScheduleCommand command) {
        Long accountId = Optional.ofNullable(command.accountId())
            .orElseThrow(() -> new InvalidAccountStateException(Map.of("reason", "ACCOUNT_ID_REQUIRED")));

        Account account = loadAccountPort.findById(AccountId.of(accountId))
            .orElseThrow(() -> new AccountNotFoundException(Map.of("accountId", accountId)));

        Product product = validateSavingsAccount(account);

        AutoTransferSchedule existingSchedule = autoTransferSchedulePort.findByAccountId(account.getId())
            .orElse(null);

        LocalDate today = LocalDate.now();
        Instant now = Instant.now();

        if (!command.enabled()) {
            if (existingSchedule != null) {
                existingSchedule.update(
                    existingSchedule.getCycle(),
                    existingSchedule.getTransferDay(),
                    existingSchedule.getAmount(),
                    false,
                    today,
                    now
                );
                autoTransferSchedulePort.update(existingSchedule);
            }
        } else {
            AutoTransferCycle cycle = Optional.ofNullable(command.cycle())
                .orElseThrow(() -> new InvalidAccountStateException(Map.of(
                    "accountId", accountId,
                    "reason", "AUTO_TRANSFER_CYCLE_REQUIRED"
                )));
            Integer transferDay = Optional.ofNullable(command.transferDay())
                .orElseThrow(() -> new InvalidAccountStateException(Map.of(
                    "accountId", accountId,
                    "reason", "AUTO_TRANSFER_DAY_REQUIRED"
                )));
            MoneyWon amount = Optional.ofNullable(command.amount())
                .orElseThrow(() -> new InvalidAccountStateException(Map.of(
                    "accountId", accountId,
                    "reason", "AUTO_TRANSFER_AMOUNT_REQUIRED"
                )));

            if (existingSchedule == null) {
                AutoTransferSchedule schedule = AutoTransferSchedule.create(
                    account.getId(),
                    cycle,
                    transferDay,
                    amount,
                    true,
                    today,
                    now
                );
                autoTransferSchedulePort.create(schedule);
            } else {
                existingSchedule.update(cycle, transferDay, amount, true, today, now);
                autoTransferSchedulePort.update(existingSchedule);
            }
        }

        AutoTransferSchedule refreshedSchedule = autoTransferSchedulePort.findByAccountId(account.getId())
            .orElse(null);

        return GetAccountResult.from(account, product, refreshedSchedule);
    }

    @Override
    public GetAccountResult closeSavingsAccount(CloseSavingsAccountCommand command) {
        Long accountId = Optional.ofNullable(command.accountId())
            .orElseThrow(() -> new InvalidAccountStateException(Map.of("reason", "ACCOUNT_ID_REQUIRED")));

        Account account = loadAccountPort.findById(AccountId.of(accountId))
            .orElseThrow(() -> new AccountNotFoundException(Map.of("accountId", accountId)));

        Product product = validateSavingsAccount(account);

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStateException(Map.of(
                "accountId", accountId,
                "currentStatus", account.getStatus(),
                "action", "CLOSE_SAVINGS_ACCOUNT"
            ));
        }

        account.close(Instant.now());

        Account saved = saveAccountPort.save(account);
        return GetAccountResult.from(saved, product);
    }

    private Product validateSavingsAccount(Account account) {
        Product product = productService.getProduct(account.getProductId());
        if (product.getCategory() != ProductCategory.INSTALLMENT_SAVINGS) {
            throw new InvalidProductTypeException(Map.of(
                "productId", product.getId(),
                "expected", ProductCategory.INSTALLMENT_SAVINGS,
                "actual", product.getCategory()
            ));
        }
        return product;
    }

    private MoneyWon resolveTargetAmount(UpdateSavingsAccountCommand command, Account account, Product product) {
        if (command.targetAmount() == null) {
            return account.getTargetAmount();
        }

        MoneyWon targetAmount = MoneyWon.of(command.targetAmount());
        if (!targetAmount.isPositive()) {
            throw new InvalidTargetAmountException(Map.of(
                "accountId", account.getId().value(),
                "targetAmount", targetAmount.amount()
            ));
        }

        if (targetAmount.isLessThan(account.getBalance())) {
            throw new InvalidTargetAmountException(Map.of(
                "accountId", account.getId().value(),
                "targetAmount", targetAmount.amount(),
                "balance", account.getBalance().amount()
            ));
        }

        Optional.ofNullable(product.getConfiguration())
            .flatMap(config -> config.getPaymentAmountRange())
            .ifPresent(range -> {
                if (!range.contains(targetAmount)) {
                    throw new InvalidTargetAmountException(Map.of(
                        "accountId", account.getId().value(),
                        "targetAmount", targetAmount.amount(),
                        "min", range.minAmount().amount(),
                        "max", range.maxAmount().amount()
                    ));
                }
            });

        return targetAmount;
    }

    private AccountNumber resolveWithdrawalAccount(UpdateSavingsAccountCommand command, Account account) {
        if (command.maturityWithdrawalAccount() == null) {
            return account.getMaturityWithdrawalAccount();
        }

        if (command.maturityWithdrawalAccount().isBlank()) {
            return null;
        }

        AccountNumber requestedAccountNumber = new AccountNumber(command.maturityWithdrawalAccount());
        Account withdrawalAccount = loadAccountPort.findByAccountNumber(requestedAccountNumber)
            .orElseThrow(() -> new InvalidWithdrawalAccountException(Map.of(
                "requestedAccount", requestedAccountNumber,
                "reason", "NOT_FOUND"
            )));

        if (!withdrawalAccount.getCustomerId().equals(account.getCustomerId())) {
            throw new InvalidWithdrawalAccountException(Map.of(
                "requestedAccount", requestedAccountNumber,
                "reason", "DIFFERENT_CUSTOMER"
            ));
        }

        Product withdrawalProduct = productService.getProduct(withdrawalAccount.getProductId());
        if (withdrawalProduct.getCategory() != ProductCategory.DEMAND_DEPOSIT) {
            throw new InvalidWithdrawalAccountException(Map.of(
                "requestedAccount", requestedAccountNumber,
                "reason", "NOT_DEMAND_DEPOSIT",
                "actualCategory", withdrawalProduct.getCategory()
            ));
        }

        if (withdrawalAccount.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidWithdrawalAccountException(Map.of(
                "requestedAccount", requestedAccountNumber,
                "reason", "TARGET_ACCOUNT_CLOSED"
            ));
        }

        return requestedAccountNumber;
    }
}
