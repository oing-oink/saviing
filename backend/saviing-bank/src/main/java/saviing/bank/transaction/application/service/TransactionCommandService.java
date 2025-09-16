package saviing.bank.transaction.application.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import saviing.bank.account.domain.model.Account;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.account.application.port.in.result.GetAccountResult;
import saviing.bank.transaction.adapter.out.account.boundary.AccountBoundaryClient;
import saviing.bank.transaction.application.port.in.CreateTransactionUseCase;
import saviing.bank.transaction.application.port.in.VoidTransactionUseCase;
import saviing.bank.transaction.application.port.in.command.CreateTransactionCommand;
import saviing.bank.transaction.application.port.in.command.CreateTransactionWithAccountNumberCommand;
import saviing.bank.transaction.application.port.in.command.VoidTransactionCommand;
import saviing.bank.transaction.application.port.in.result.TransactionResult;
import saviing.bank.transaction.exception.TransactionNotFoundException;
import saviing.bank.transaction.exception.DuplicateTransactionException;
import saviing.bank.account.exception.AccountNotFoundException;

import java.util.Map;
import saviing.bank.transaction.application.port.out.LoadTransactionPort;
import saviing.bank.transaction.application.port.out.SaveTransactionPort;
import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.service.TransactionValidationService;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionCommandService implements CreateTransactionUseCase, VoidTransactionUseCase {

    private final AccountBoundaryClient accountBoundaryClient;
    private final LoadTransactionPort loadTransactionPort;
    private final SaveTransactionPort saveTransactionPort;
    private final TransactionValidationService validationService;

    @Override
    public TransactionResult createTransaction(CreateTransactionCommand command) {
        Account account = accountBoundaryClient.getById(command.accountId());

        validationService.validateTransactionAmount(command.amount(), command.transactionType());
        validationService.validateValueDate(command.valueDate());
        validationService.validateAccountStatus(account);

        if (command.idempotencyKey() != null) {
            if (loadTransactionPort.existsByIdempotencyKey(command.accountId(), command.idempotencyKey())) {
                return getExistingTransaction(command.accountId(), command.idempotencyKey());
            }
        }

        if (command.direction() == TransactionDirection.DEBIT) {
            validationService.validateDebitTransaction(account, command.amount());
        }

        Instant now = Instant.now();
        Transaction transaction = Transaction.create(
            command.accountId(),
            command.transactionType(),
            command.direction(),
            command.amount(),
            command.valueDate(),
            now,
            command.idempotencyKey(),
            command.description()
        );

        var transactionId = saveTransactionPort.saveTransaction(transaction);

        accountBoundaryClient.applyBalanceEffect(account.getId().value(), command.amount(), command.direction());

        transaction = loadTransactionPort.loadTransaction(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException(
                Map.of("message", "생성된 거래를 찾을 수 없습니다")
            ));

        return mapToResult(transaction);
    }

    @Override
    public TransactionResult createTransaction(CreateTransactionWithAccountNumberCommand command) {
        GetAccountResult account;
        try {
            account = accountBoundaryClient.getByNumber(command.accountNumber());
        } catch (AccountNotFoundException e) {
            throw new TransactionNotFoundException(
                java.util.Map.of("accountNumber", command.accountNumber())
            );
        }
        CreateTransactionCommand accountIdCommand = CreateTransactionCommand.builder()
            .accountId(account.accountId())
            .transactionType(command.transactionType())
            .direction(command.direction())
            .amount(command.amount())
            .valueDate(command.valueDate())
            .idempotencyKey(command.idempotencyKey())
            .description(command.description())
            .build();
        return createTransaction(accountIdCommand);
    }

    @Override
    public TransactionResult voidTransaction(VoidTransactionCommand command) {
        Transaction transaction = loadTransactionPort.loadTransaction(command.transactionId())
            .orElseThrow(() -> new TransactionNotFoundException(
                Map.of("transactionId", command.transactionId().value())
            ));

        Account account = accountBoundaryClient.getById(transaction.getAccountId());

        transaction.voidTransaction(Instant.now());
        saveTransactionPort.updateTransaction(transaction);

        // 무효화 시 잔액 영향 반대로 적용
        var reverseDirection = transaction.getDirection() == TransactionDirection.CREDIT
            ? TransactionDirection.DEBIT : TransactionDirection.CREDIT;
        accountBoundaryClient.applyBalanceEffect(account.getId().value(), transaction.getAmount(), reverseDirection);

        return mapToResult(transaction);
    }

    private TransactionResult getExistingTransaction(
        Long accountId,
        saviing.bank.transaction.domain.vo.IdempotencyKey idempotencyKey
    ) {
        Transaction existing = loadTransactionPort.loadTransactionByIdempotencyKey(accountId, idempotencyKey)
            .orElseThrow(() -> new DuplicateTransactionException(
                Map.of(
                    "accountId", accountId,
                    "idempotencyKey", idempotencyKey.value()
                )
            ));
        return mapToResult(existing);
    }

    // 잔액 처리 로직은 AccountBoundaryClient로 이동하여 경계를 명확히 유지합니다.

    private TransactionResult mapToResult(Transaction transaction) {
        return TransactionResult.from(transaction);
    }
}
