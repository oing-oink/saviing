package saviing.bank.transaction.application.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import saviing.bank.account.api.AccountInternalApi;
import saviing.bank.account.api.request.DepositAccountRequest;
import saviing.bank.account.api.request.WithdrawAccountRequest;
import saviing.bank.account.api.request.GetAccountRequest;
import saviing.bank.account.api.response.AccountApiResponse;
import saviing.bank.account.api.response.AccountInfoResponse;
import saviing.bank.account.api.response.BalanceUpdateResponse;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.application.port.in.CreateTransactionUseCase;
import saviing.bank.transaction.application.port.in.VoidTransactionUseCase;
import saviing.bank.transaction.application.port.in.command.CreateTransactionCommand;
import saviing.bank.transaction.application.port.in.command.CreateTransactionWithAccountNumberCommand;
import saviing.bank.transaction.application.port.in.command.VoidTransactionCommand;
import saviing.bank.transaction.application.port.in.result.TransactionResult;
import saviing.bank.transaction.application.port.out.LoadTransactionPort;
import saviing.bank.transaction.application.port.out.SaveTransactionPort;
import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.service.TransactionValidationService;
import saviing.bank.transaction.exception.TransactionNotFoundException;
import saviing.bank.transaction.exception.DuplicateTransactionException;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionCommandService implements CreateTransactionUseCase, VoidTransactionUseCase {

    private final AccountInternalApi accountInternalApi;
    private final LoadTransactionPort loadTransactionPort;
    private final SaveTransactionPort saveTransactionPort;
    private final TransactionValidationService validationService;

    @Override
    public TransactionResult createTransaction(CreateTransactionCommand command) {
        AccountApiResponse<AccountInfoResponse> accountResponse = accountInternalApi.getAccount(
            GetAccountRequest.of(command.accountId())
        );

        if (accountResponse.isFailure()) {
            throw new TransactionNotFoundException(
                Map.of("accountId", command.accountId(), "message", ((AccountApiResponse.Failure<AccountInfoResponse>) accountResponse).message())
            );
        }

        AccountInfoResponse accountInfo = ((AccountApiResponse.Success<AccountInfoResponse>) accountResponse).data();

        validationService.validateTransactionAmount(command.amount(), command.transactionType());
        validationService.validateValueDate(command.valueDate());
        validationService.validateAccountStatusByString(accountInfo.status());

        if (command.idempotencyKey() != null) {
            if (loadTransactionPort.existsByIdempotencyKey(command.accountId(), command.idempotencyKey())) {
                return getExistingTransaction(command.accountId(), command.idempotencyKey());
            }
        }

        if (command.direction() == TransactionDirection.DEBIT) {
            validationService.validateDebitTransactionByBalance(MoneyWon.of(accountInfo.balance()), command.amount());
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

        applyBalanceEffect(command.accountId(), command.amount(), command.direction());

        transaction = loadTransactionPort.loadTransaction(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException(
                Map.of("message", "생성된 거래를 찾을 수 없습니다")
            ));

        return mapToResult(transaction);
    }

    @Override
    public TransactionResult createTransaction(CreateTransactionWithAccountNumberCommand command) {
        AccountApiResponse<AccountInfoResponse> accountResponse = getAccountByNumber(command.accountNumber());

        if (accountResponse.isFailure()) {
            throw new TransactionNotFoundException(
                java.util.Map.of("accountNumber", command.accountNumber())
            );
        }

        AccountInfoResponse account = ((AccountApiResponse.Success<AccountInfoResponse>) accountResponse).data();
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

        AccountApiResponse<AccountInfoResponse> accountResponse = accountInternalApi.getAccount(
            GetAccountRequest.of(transaction.getAccountId())
        );

        if (accountResponse.isFailure()) {
            throw new TransactionNotFoundException(
                Map.of("accountId", transaction.getAccountId(), "message", ((AccountApiResponse.Failure<AccountInfoResponse>) accountResponse).message())
            );
        }

        AccountInfoResponse account = ((AccountApiResponse.Success<AccountInfoResponse>) accountResponse).data();

        transaction.voidTransaction(Instant.now());
        saveTransactionPort.updateTransaction(transaction);

        // 무효화 시 잔액 영향 반대로 적용
        var reverseDirection = transaction.getDirection() == TransactionDirection.CREDIT
            ? TransactionDirection.DEBIT : TransactionDirection.CREDIT;
        applyBalanceEffect(account.accountId(), transaction.getAmount(), reverseDirection);

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

    private void applyBalanceEffect(Long accountId, MoneyWon amount, TransactionDirection direction) {
        AccountApiResponse<BalanceUpdateResponse> response;

        if (direction == TransactionDirection.CREDIT) {
            response = accountInternalApi.deposit(DepositAccountRequest.of(accountId, amount.amount()));
        } else {
            response = accountInternalApi.withdraw(WithdrawAccountRequest.of(accountId, amount.amount()));
        }

        if (response.isFailure()) {
            throw new RuntimeException("Failed to apply balance effect: " + ((AccountApiResponse.Failure<BalanceUpdateResponse>) response).message());
        }
    }

    private AccountApiResponse<AccountInfoResponse> getAccountByNumber(String accountNumber) {
        // TODO: AccountInternalApi에 계좌번호로 조회하는 메서드가 필요함
        // 현재는 임시로 예외 발생
        throw new TransactionNotFoundException(
            Map.of("accountNumber", accountNumber, "message", "AccountInternalApi does not support getByNumber yet")
        );
    }

    private TransactionResult mapToResult(Transaction transaction) {
        return TransactionResult.from(transaction);
    }
}
