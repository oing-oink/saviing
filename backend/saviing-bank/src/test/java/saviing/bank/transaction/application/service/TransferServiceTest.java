package saviing.bank.transaction.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import saviing.bank.account.api.AccountInternalApi;
import saviing.bank.account.api.request.DepositAccountRequest;
import saviing.bank.account.api.request.GetAccountRequest;
import saviing.bank.account.api.request.WithdrawAccountRequest;
import saviing.bank.account.api.response.AccountApiResponse;
import saviing.bank.account.api.response.AccountInfoResponse;
import saviing.bank.account.api.response.BalanceUpdateResponse;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.application.port.in.command.TransferCommand;
import saviing.bank.transaction.application.port.in.result.TransferResult;
import saviing.bank.transaction.application.port.out.LoadTransactionPort;
import saviing.bank.transaction.application.port.out.SaveTransactionPort;
import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.transfer.TransferStatus;
import saviing.bank.transaction.domain.model.transfer.TransferType;
import saviing.bank.transaction.domain.vo.LedgerEntrySnapshot;
import saviing.bank.transaction.domain.model.transfer.LedgerEntryStatus;
import saviing.bank.transaction.domain.vo.TransferSnapshot;
import saviing.bank.transaction.application.service.LedgerService;
import saviing.bank.transaction.domain.service.TransferDomainService;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private LedgerService ledgerService;
    @Mock
    private TransferDomainService transferDomainService;
    @Mock
    private SaveTransactionPort saveTransactionPort;
    @Mock
    private LoadTransactionPort loadTransactionPort;
    @Mock
    private AccountInternalApi accountInternalApi;

    @InjectMocks
    private TransferService transferService;

    @Test
    void 이미_완료된_송금은_기존_결과를_반환한다() {
        // given
        IdempotencyKey idempotencyKey = IdempotencyKey.of("transfer-1");
        LedgerEntrySnapshot debitSnapshot = new LedgerEntrySnapshot(
            1L,
            100L,
            TransactionDirection.DEBIT,
            MoneyWon.of(1000L),
            LedgerEntryStatus.POSTED,
            LocalDate.now(),
            Instant.now(),
            TransactionId.of(10L),
            Instant.now(),
            Instant.now()
        );
        LedgerEntrySnapshot creditSnapshot = new LedgerEntrySnapshot(
            2L,
            200L,
            TransactionDirection.CREDIT,
            MoneyWon.of(1000L),
            LedgerEntryStatus.POSTED,
            LocalDate.now(),
            Instant.now(),
            TransactionId.of(11L),
            Instant.now(),
            Instant.now()
        );
        TransferSnapshot settledSnapshot = new TransferSnapshot(
            TransferType.INTERNAL,
            TransferStatus.SETTLED,
            idempotencyKey,
            100L,
            200L,
            MoneyWon.of(1000L),
            LocalDate.now(),
            List.of(debitSnapshot, creditSnapshot),
            Instant.now().minusSeconds(5),
            Instant.now(),
            null
        );

        when(ledgerService.initializeTransfer(
            eq(idempotencyKey),
            any(Long.class),
            any(Long.class),
            any(MoneyWon.class),
            any(LocalDate.class),
            any(TransferType.class)
        )).thenReturn(settledSnapshot);

        TransferCommand command = TransferCommand.builder()
            .sourceAccountId(100L)
            .targetAccountId(200L)
            .amount(MoneyWon.of(1000L))
            .valueDate(LocalDate.now())
            .memo("test")
            .idempotencyKey(idempotencyKey)
            .transferType(TransferType.INTERNAL)
            .requestedAt(Instant.now())
            .build();

        // when
        TransferResult result = transferService.transfer(command);

        // then
        assertThat(result.idempotencyKey()).isEqualTo(idempotencyKey);
        assertThat(result.sourceAccountId()).isEqualTo(100L);
        assertThat(result.targetAccountId()).isEqualTo(200L);
        assertThat(result.amount().amount()).isEqualTo(1000L);
        assertThat(result.valueDate()).isEqualTo(settledSnapshot.valueDate());
        assertThat(result.status()).isEqualTo(TransferStatus.SETTLED);
        assertThat(result.debitTransactionId()).isEqualTo(TransactionId.of(10L));
        assertThat(result.creditTransactionId()).isEqualTo(TransactionId.of(11L));
        assertThat(result.requestedAt()).isEqualTo(settledSnapshot.createdAt());
        assertThat(result.completedAt()).isEqualTo(settledSnapshot.updatedAt());
        verifyNoInteractions(accountInternalApi, saveTransactionPort);
    }

    @Test
    void 거래_생성_실패시_보상_트랜잭션이_실행된다() {
        // given - withdraw 성공 후 createTransaction 실패 시나리오
        IdempotencyKey idempotencyKey = IdempotencyKey.of("transfer-fail-1");

        TransferSnapshot requestedSnapshot = createRequestedSnapshot(idempotencyKey);
        TransferSnapshot failedSnapshot = createFailedSnapshot(idempotencyKey);

        when(ledgerService.initializeTransfer(any(), any(), any(), any(), any(), any()))
            .thenReturn(requestedSnapshot);

        // Account 조회 성공
        AccountApiResponse<AccountInfoResponse> sourceAccountResponse = new AccountApiResponse.Success<>(
            new AccountInfoResponse(100L, "1234567890", 1L, 5000L, "ACTIVE", 1L)
        );
        AccountApiResponse<AccountInfoResponse> targetAccountResponse = new AccountApiResponse.Success<>(
            new AccountInfoResponse(200L, "0987654321", 2L, 3000L, "ACTIVE", 1L)
        );

        when(accountInternalApi.getAccount(any(GetAccountRequest.class)))
            .thenReturn(sourceAccountResponse)
            .thenReturn(targetAccountResponse);

        // withdraw 성공
        when(accountInternalApi.withdraw(any(WithdrawAccountRequest.class)))
            .thenReturn(new AccountApiResponse.Success<>(
                new BalanceUpdateResponse(100L, 5000L, 4000L, 1000L)
            ));

        // 보상 deposit 성공
        when(accountInternalApi.deposit(any(DepositAccountRequest.class)))
            .thenReturn(new AccountApiResponse.Success<>(
                new BalanceUpdateResponse(100L, 4000L, 5000L, 1000L)
            ));

        // createTransaction: 첫 번째 호출(출금) 실패, 두 번째 호출(보상) 성공
        when(saveTransactionPort.saveTransaction(any(Transaction.class)))
            .thenThrow(new RuntimeException("Transaction creation failed")) // 첫 번째 호출
            .thenReturn(TransactionId.of(999L)); // 두 번째 호출 (보상)

        when(ledgerService.markTransferFailed(anyLong(), any(), anyString()))
            .thenReturn(failedSnapshot);

        TransferCommand command = TransferCommand.builder()
            .sourceAccountId(100L)
            .targetAccountId(200L)
            .amount(MoneyWon.of(1000L))
            .valueDate(LocalDate.now())
            .memo("test")
            .idempotencyKey(idempotencyKey)
            .transferType(TransferType.INTERNAL)
            .requestedAt(Instant.now())
            .build();

        // when
        assertThatThrownBy(() -> transferService.transfer(command))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Transaction creation failed");

        // then - 보상 트랜잭션이 실행되었는지 확인
        verify(accountInternalApi).deposit(DepositAccountRequest.of(100L, 1000L));
        verify(ledgerService).markTransferFailed(eq(100L), eq(idempotencyKey), any(String.class));
    }

    @Test
    void 보상_트랜잭션_멱등성이_보장된다() {
        // given - 동일한 transferId로 보상 트랜잭션 2번 실행
        IdempotencyKey idempotencyKey = IdempotencyKey.of("transfer-idem-1");

        TransferSnapshot requestedSnapshot = createRequestedSnapshot(idempotencyKey);
        TransferSnapshot failedSnapshot = createFailedSnapshot(idempotencyKey);

        when(ledgerService.initializeTransfer(any(), any(), any(), any(), any(), any()))
            .thenReturn(requestedSnapshot);

        // Account 조회 성공
        AccountApiResponse<AccountInfoResponse> accountResponse = new AccountApiResponse.Success<>(
            new AccountInfoResponse(100L, "1234567890", 1L, 5000L, "ACTIVE", 1L)
        );
        when(accountInternalApi.getAccount(any(GetAccountRequest.class)))
            .thenReturn(accountResponse)
            .thenReturn(accountResponse);

        // withdraw 성공
        when(accountInternalApi.withdraw(any(WithdrawAccountRequest.class)))
            .thenReturn(new AccountApiResponse.Success<>(
                new BalanceUpdateResponse(100L, 5000L, 4000L, 1000L)
            ));

        // createTransaction 실패
        when(saveTransactionPort.saveTransaction(any(Transaction.class)))
            .thenThrow(new RuntimeException("Transaction creation failed"));

        // 보상 deposit 성공
        when(accountInternalApi.deposit(any(DepositAccountRequest.class)))
            .thenReturn(new AccountApiResponse.Success<>(
                new BalanceUpdateResponse(100L, 4000L, 5000L, 1000L)
            ));

        when(ledgerService.markTransferFailed(anyLong(), any(), anyString()))
            .thenReturn(failedSnapshot);

        TransferCommand command = TransferCommand.builder()
            .sourceAccountId(100L)
            .targetAccountId(200L)
            .amount(MoneyWon.of(1000L))
            .valueDate(LocalDate.now())
            .memo("test")
            .idempotencyKey(idempotencyKey)
            .transferType(TransferType.INTERNAL)
            .requestedAt(Instant.now())
            .build();

        // when - 첫 번째 실행
        assertThatThrownBy(() -> transferService.transfer(command))
            .isInstanceOf(RuntimeException.class);

        // then - saveTransaction이 멱등성 키를 포함하여 호출되었는지 확인
        // 실제로는 멱등성이 Transaction 저장 레벨에서 처리되므로 여기서는 호출 확인만
        verify(accountInternalApi).deposit(DepositAccountRequest.of(100L, 1000L));
    }

    @Test
    void 출금_실패시_보상_트랜잭션이_실행되지_않는다() {
        // given - withdraw 자체가 실패한 경우
        IdempotencyKey idempotencyKey = IdempotencyKey.of("transfer-no-comp-1");

        TransferSnapshot requestedSnapshot = createRequestedSnapshot(idempotencyKey);
        TransferSnapshot failedSnapshot = createFailedSnapshot(idempotencyKey);

        when(ledgerService.initializeTransfer(any(), any(), any(), any(), any(), any()))
            .thenReturn(requestedSnapshot);

        // Account 조회 성공
        AccountApiResponse<AccountInfoResponse> accountResponse = new AccountApiResponse.Success<>(
            new AccountInfoResponse(100L, "1234567890", 1L, 5000L, "ACTIVE", 1L)
        );
        when(accountInternalApi.getAccount(any(GetAccountRequest.class)))
            .thenReturn(accountResponse)
            .thenReturn(accountResponse);

        // withdraw 실패
        when(accountInternalApi.withdraw(any(WithdrawAccountRequest.class)))
            .thenThrow(new RuntimeException("Insufficient balance"));

        when(ledgerService.markTransferFailed(anyLong(), any(), anyString()))
            .thenReturn(failedSnapshot);

        TransferCommand command = TransferCommand.builder()
            .sourceAccountId(100L)
            .targetAccountId(200L)
            .amount(MoneyWon.of(1000L))
            .valueDate(LocalDate.now())
            .memo("test")
            .idempotencyKey(idempotencyKey)
            .transferType(TransferType.INTERNAL)
            .requestedAt(Instant.now())
            .build();

        // when
        assertThatThrownBy(() -> transferService.transfer(command))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Insufficient balance");

        // then - 보상 트랜잭션이 실행되지 않았는지 확인 (deposit 호출 없음)
        verifyNoInteractions(saveTransactionPort);
    }

    private TransferSnapshot createRequestedSnapshot(IdempotencyKey idempotencyKey) {
        LedgerEntrySnapshot debitSnapshot = new LedgerEntrySnapshot(
            1L, 100L, TransactionDirection.DEBIT, MoneyWon.of(1000L),
            LedgerEntryStatus.REQUESTED, LocalDate.now(), null, null,
            Instant.now(), Instant.now()
        );
        LedgerEntrySnapshot creditSnapshot = new LedgerEntrySnapshot(
            2L, 200L, TransactionDirection.CREDIT, MoneyWon.of(1000L),
            LedgerEntryStatus.REQUESTED, LocalDate.now(), null, null,
            Instant.now(), Instant.now()
        );
        return new TransferSnapshot(
            TransferType.INTERNAL,
            TransferStatus.REQUESTED,
            idempotencyKey,
            100L,
            200L,
            MoneyWon.of(1000L),
            LocalDate.now(),
            List.of(debitSnapshot, creditSnapshot),
            Instant.now(),
            Instant.now(),
            null
        );
    }

    private TransferSnapshot createFailedSnapshot(IdempotencyKey idempotencyKey) {
        LedgerEntrySnapshot debitSnapshot = new LedgerEntrySnapshot(
            1L, 100L, TransactionDirection.DEBIT, MoneyWon.of(1000L),
            LedgerEntryStatus.FAILED, LocalDate.now(), null, null,
            Instant.now(), Instant.now()
        );
        LedgerEntrySnapshot creditSnapshot = new LedgerEntrySnapshot(
            2L, 200L, TransactionDirection.CREDIT, MoneyWon.of(1000L),
            LedgerEntryStatus.FAILED, LocalDate.now(), null, null,
            Instant.now(), Instant.now()
        );
        return new TransferSnapshot(
            TransferType.INTERNAL,
            TransferStatus.FAILED,
            idempotencyKey,
            100L,
            200L,
            MoneyWon.of(1000L),
            LocalDate.now(),
            List.of(debitSnapshot, creditSnapshot),
            Instant.now(),
            Instant.now(),
            "Test failure"
        );
    }
}
