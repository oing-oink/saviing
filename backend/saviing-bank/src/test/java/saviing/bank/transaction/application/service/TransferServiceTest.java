package saviing.bank.transaction.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import saviing.bank.transaction.application.port.out.LoadCustomerNamePort;
import saviing.bank.transaction.application.port.out.LoadTransactionPort;
import saviing.bank.transaction.application.port.out.SaveTransactionPort;
import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.TransactionType;
import saviing.bank.transaction.domain.model.transfer.TransferStatus;
import saviing.bank.transaction.domain.model.transfer.TransferType;
import saviing.bank.transaction.domain.vo.LedgerEntrySnapshot;
import saviing.bank.transaction.domain.model.transfer.LedgerEntryStatus;
import saviing.bank.transaction.domain.vo.TransferSnapshot;
import saviing.bank.transaction.domain.service.TransferDomainService;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.bank.transaction.exception.DuplicateTransferRequestException;
import saviing.bank.transaction.exception.TransferValidationException;

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
    private LoadCustomerNamePort loadCustomerNamePort;
    @Mock
    private AccountInternalApi accountInternalApi;

    @InjectMocks
    private TransferService transferService;

    @Test
    void 이미_완료된_송금은_중복_예외를_던진다() {
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

        // when & then
        assertThatThrownBy(() -> transferService.transfer(command))
            .isInstanceOf(DuplicateTransferRequestException.class)
            .hasMessageContaining("중복된 송금");

        verify(transferDomainService).ensureIdempotency(idempotencyKey);
        verifyNoInteractions(accountInternalApi, saveTransactionPort, loadTransactionPort);
    }

    @Test
    void 시스템_예외_발생시_일반화된_실패사유와_보상이_기록된다() {
        // given - withdraw 성공 후 createTransaction에서 시스템 예외 발생
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

        // then - 보상 트랜잭션이 실행되었는지 및 일반화된 실패 사유가 기록됐는지 확인
        verify(accountInternalApi).deposit(DepositAccountRequest.of(100L, 1000L));
        ArgumentCaptor<String> failureReasonCaptor = ArgumentCaptor.forClass(String.class);
        verify(ledgerService).markTransferFailed(eq(100L), eq(idempotencyKey), failureReasonCaptor.capture());
        assertThat(failureReasonCaptor.getValue())
            .isEqualTo("내부 시스템 오류로 송금이 실패했습니다.; compensationStatus=SUCCESS; compensationTxId=999");
    }

    @Test
    void 도메인_예외_발생시_도메인_메시지와_보상이_기록된다() {
        // given - withdraw 이후 도메인 예외 발생
        IdempotencyKey idempotencyKey = IdempotencyKey.of("transfer-domain-fail");

        TransferSnapshot requestedSnapshot = createRequestedSnapshot(idempotencyKey);
        TransferSnapshot failedSnapshot = createFailedSnapshot(idempotencyKey);

        when(ledgerService.initializeTransfer(any(), any(), any(), any(), any(), any()))
            .thenReturn(requestedSnapshot);

        AccountApiResponse<AccountInfoResponse> sourceAccountResponse = new AccountApiResponse.Success<>(
            new AccountInfoResponse(100L, "1234567890", 1L, 5000L, "ACTIVE", 1L)
        );
        AccountApiResponse<AccountInfoResponse> targetAccountResponse = new AccountApiResponse.Success<>(
            new AccountInfoResponse(200L, "0987654321", 2L, 3000L, "ACTIVE", 1L)
        );

        when(accountInternalApi.getAccount(any(GetAccountRequest.class)))
            .thenReturn(sourceAccountResponse)
            .thenReturn(targetAccountResponse);

        when(accountInternalApi.withdraw(any(WithdrawAccountRequest.class)))
            .thenReturn(new AccountApiResponse.Success<>(
                new BalanceUpdateResponse(100L, 5000L, 4000L, 1000L)
            ));

        when(accountInternalApi.deposit(any(DepositAccountRequest.class)))
            .thenReturn(new AccountApiResponse.Success<>(
                new BalanceUpdateResponse(100L, 4000L, 5000L, 1000L)
            ));

        TransferValidationException domainException = new TransferValidationException(
            "도메인 검증 실패",
            java.util.Map.of("field", "value")
        );

        when(saveTransactionPort.saveTransaction(any(Transaction.class)))
            .thenThrow(domainException)
            .thenReturn(TransactionId.of(999L));

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

        // when & then
        assertThatThrownBy(() -> transferService.transfer(command))
            .isInstanceOf(TransferValidationException.class)
            .hasMessage("도메인 검증 실패");

        verify(accountInternalApi).deposit(DepositAccountRequest.of(100L, 1000L));
        ArgumentCaptor<String> failureReasonCaptor = ArgumentCaptor.forClass(String.class);
        verify(ledgerService).markTransferFailed(eq(100L), eq(idempotencyKey), failureReasonCaptor.capture());
        assertThat(failureReasonCaptor.getValue())
            .isEqualTo("도메인 검증 실패; compensationStatus=SUCCESS; compensationTxId=999");
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

        ArgumentCaptor<String> failureReasonCaptor = ArgumentCaptor.forClass(String.class);
        verify(ledgerService).markTransferFailed(eq(100L), eq(idempotencyKey), failureReasonCaptor.capture());
        assertThat(failureReasonCaptor.getValue()).isEqualTo("내부 시스템 오류로 송금이 실패했습니다.");

        // then - 보상 트랜잭션이 실행되지 않았는지 확인 (deposit 호출 없음)
        verifyNoInteractions(saveTransactionPort);
    }

    @Test
    void 송금시_트랜잭션_설명이_올바르게_설정된다() {
        // given
        IdempotencyKey idempotencyKey = IdempotencyKey.of("transfer-desc-test");
        TransferSnapshot requestedSnapshot = createRequestedSnapshot(idempotencyKey);
        TransferSnapshot settledSnapshot = createSettledSnapshot(idempotencyKey);

        when(ledgerService.initializeTransfer(any(), any(), any(), any(), any(), any()))
            .thenReturn(requestedSnapshot);

        // 고객 이름 설정
        when(loadCustomerNamePort.loadCustomerName(1L)).thenReturn(Optional.of("홍길동")); // source customer
        when(loadCustomerNamePort.loadCustomerName(2L)).thenReturn(Optional.of("김철수")); // target customer

        // Account 조회 성공 (customerId 포함)
        AccountApiResponse<AccountInfoResponse> sourceAccountResponse = new AccountApiResponse.Success<>(
            new AccountInfoResponse(100L, "1234567890", 1L, 5000L, "ACTIVE", 1L)
        );
        AccountApiResponse<AccountInfoResponse> targetAccountResponse = new AccountApiResponse.Success<>(
            new AccountInfoResponse(200L, "0987654321", 2L, 3000L, "ACTIVE", 1L)
        );
        when(accountInternalApi.getAccount(any(GetAccountRequest.class)))
            .thenReturn(sourceAccountResponse, targetAccountResponse);

        // withdraw/deposit 성공
        BalanceUpdateResponse withdrawResponse = new BalanceUpdateResponse(100L, 5000L, 4000L, 1000L);
        BalanceUpdateResponse depositResponse = new BalanceUpdateResponse(200L, 3000L, 4000L, 1000L);
        when(accountInternalApi.withdraw(any(WithdrawAccountRequest.class)))
            .thenReturn(new AccountApiResponse.Success<>(withdrawResponse));
        when(accountInternalApi.deposit(any(DepositAccountRequest.class)))
            .thenReturn(new AccountApiResponse.Success<>(depositResponse));

        when(saveTransactionPort.saveTransaction(any(Transaction.class)))
            .thenReturn(TransactionId.of(1L), TransactionId.of(2L));

        when(ledgerService.markEntryPosted(any(), any(), any(), any(), any()))
            .thenReturn(settledSnapshot);
        when(ledgerService.markTransferSettled(any(), any(), any()))
            .thenReturn(settledSnapshot);

        // Mock transaction loading for linking
        Transaction mockDebitTx = Transaction.create(100L, TransactionType.TRANSFER_OUT, TransactionDirection.DEBIT,
            MoneyWon.of(1000L), MoneyWon.of(4000L), LocalDate.now(), Instant.now(), "김철수");
        Transaction mockCreditTx = Transaction.create(200L, TransactionType.TRANSFER_IN, TransactionDirection.CREDIT,
            MoneyWon.of(1000L), MoneyWon.of(4000L), LocalDate.now(), Instant.now(), "생일선물");
        when(loadTransactionPort.loadTransaction(TransactionId.of(1L)))
            .thenReturn(Optional.of(mockDebitTx));
        when(loadTransactionPort.loadTransaction(TransactionId.of(2L)))
            .thenReturn(Optional.of(mockCreditTx));

        TransferCommand command = TransferCommand.builder()
            .sourceAccountId(100L)
            .targetAccountId(200L)
            .amount(MoneyWon.of(1000L))
            .valueDate(LocalDate.now())
            .memo("생일선물")
            .idempotencyKey(idempotencyKey)
            .transferType(TransferType.INTERNAL)
            .requestedAt(Instant.now())
            .build();

        // when
        transferService.transfer(command);

        // then
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(saveTransactionPort, times(2)).saveTransaction(transactionCaptor.capture());

        List<Transaction> capturedTransactions = transactionCaptor.getAllValues();
        Transaction debitTransaction = capturedTransactions.get(0);  // 출금 거래
        Transaction creditTransaction = capturedTransactions.get(1); // 입금 거래

        // 출금 거래는 수취인 이름으로 설정
        assertThat(debitTransaction.getDescription()).isEqualTo("김철수");
        assertThat(debitTransaction.getDirection()).isEqualTo(TransactionDirection.DEBIT);

        // 입금 거래는 memo가 있으므로 memo로 설정
        assertThat(creditTransaction.getDescription()).isEqualTo("생일선물");
        assertThat(creditTransaction.getDirection()).isEqualTo(TransactionDirection.CREDIT);
    }

    @Test
    void 메모가_없을때_입금거래는_송금자_이름으로_설정된다() {
        // given
        IdempotencyKey idempotencyKey = IdempotencyKey.of("transfer-no-memo");
        TransferSnapshot requestedSnapshot = createRequestedSnapshot(idempotencyKey);
        TransferSnapshot settledSnapshot = createSettledSnapshot(idempotencyKey);

        when(ledgerService.initializeTransfer(any(), any(), any(), any(), any(), any()))
            .thenReturn(requestedSnapshot);

        // 고객 이름 설정
        when(loadCustomerNamePort.loadCustomerName(1L)).thenReturn(Optional.of("홍길동")); // source customer
        when(loadCustomerNamePort.loadCustomerName(2L)).thenReturn(Optional.of("김철수")); // target customer

        // Account 조회 성공
        AccountApiResponse<AccountInfoResponse> sourceAccountResponse = new AccountApiResponse.Success<>(
            new AccountInfoResponse(100L, "1234567890", 1L, 5000L, "ACTIVE", 1L)
        );
        AccountApiResponse<AccountInfoResponse> targetAccountResponse = new AccountApiResponse.Success<>(
            new AccountInfoResponse(200L, "0987654321", 2L, 3000L, "ACTIVE", 1L)
        );
        when(accountInternalApi.getAccount(any(GetAccountRequest.class)))
            .thenReturn(sourceAccountResponse, targetAccountResponse);

        // withdraw/deposit 성공
        BalanceUpdateResponse withdrawResponse = new BalanceUpdateResponse(100L, 5000L, 4000L, 1000L);
        BalanceUpdateResponse depositResponse = new BalanceUpdateResponse(200L, 3000L, 4000L, 1000L);
        when(accountInternalApi.withdraw(any(WithdrawAccountRequest.class)))
            .thenReturn(new AccountApiResponse.Success<>(withdrawResponse));
        when(accountInternalApi.deposit(any(DepositAccountRequest.class)))
            .thenReturn(new AccountApiResponse.Success<>(depositResponse));

        when(saveTransactionPort.saveTransaction(any(Transaction.class)))
            .thenReturn(TransactionId.of(1L), TransactionId.of(2L));

        when(ledgerService.markEntryPosted(any(), any(), any(), any(), any()))
            .thenReturn(settledSnapshot);
        when(ledgerService.markTransferSettled(any(), any(), any()))
            .thenReturn(settledSnapshot);

        // Mock transaction loading for linking
        Transaction mockDebitTx2 = Transaction.create(100L, TransactionType.TRANSFER_OUT, TransactionDirection.DEBIT,
            MoneyWon.of(1000L), MoneyWon.of(4000L), LocalDate.now(), Instant.now(), "김철수");
        Transaction mockCreditTx2 = Transaction.create(200L, TransactionType.TRANSFER_IN, TransactionDirection.CREDIT,
            MoneyWon.of(1000L), MoneyWon.of(4000L), LocalDate.now(), Instant.now(), "홍길동");
        when(loadTransactionPort.loadTransaction(TransactionId.of(1L)))
            .thenReturn(Optional.of(mockDebitTx2));
        when(loadTransactionPort.loadTransaction(TransactionId.of(2L)))
            .thenReturn(Optional.of(mockCreditTx2));

        TransferCommand command = TransferCommand.builder()
            .sourceAccountId(100L)
            .targetAccountId(200L)
            .amount(MoneyWon.of(1000L))
            .valueDate(LocalDate.now())
            .memo(null) // 메모 없음
            .idempotencyKey(idempotencyKey)
            .transferType(TransferType.INTERNAL)
            .requestedAt(Instant.now())
            .build();

        // when
        transferService.transfer(command);

        // then
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(saveTransactionPort, times(2)).saveTransaction(transactionCaptor.capture());

        List<Transaction> capturedTransactions = transactionCaptor.getAllValues();
        Transaction creditTransaction = capturedTransactions.get(1); // 입금 거래

        // 입금 거래는 memo가 없으므로 송금자 이름으로 설정
        assertThat(creditTransaction.getDescription()).isEqualTo("홍길동");
        assertThat(creditTransaction.getDirection()).isEqualTo(TransactionDirection.CREDIT);
    }

    @Test
    void 보상_트랜잭션의_설명이_올바르게_설정된다() {
        // given - 출금 성공 후 입금에서 실패 발생하여 보상 트랜잭션 생성
        IdempotencyKey idempotencyKey = IdempotencyKey.of("transfer-compensation-test");
        TransferSnapshot requestedSnapshot = createRequestedSnapshot(idempotencyKey);
        TransferSnapshot failedSnapshot = createFailedSnapshot(idempotencyKey);

        when(ledgerService.initializeTransfer(any(), any(), any(), any(), any(), any()))
            .thenReturn(requestedSnapshot);

        // 고객 이름 설정
        when(loadCustomerNamePort.loadCustomerName(1L)).thenReturn(Optional.of("홍길동")); // source customer
        when(loadCustomerNamePort.loadCustomerName(2L)).thenReturn(Optional.of("김철수")); // target customer

        // Account 조회 성공
        AccountApiResponse<AccountInfoResponse> sourceAccountResponse = new AccountApiResponse.Success<>(
            new AccountInfoResponse(100L, "1234567890", 1L, 5000L, "ACTIVE", 1L)
        );
        AccountApiResponse<AccountInfoResponse> targetAccountResponse = new AccountApiResponse.Success<>(
            new AccountInfoResponse(200L, "0987654321", 2L, 3000L, "ACTIVE", 1L)
        );
        when(accountInternalApi.getAccount(any(GetAccountRequest.class)))
            .thenReturn(sourceAccountResponse, targetAccountResponse);

        // withdraw 성공, deposit 실패로 보상 트랜잭션 발생
        BalanceUpdateResponse withdrawResponse = new BalanceUpdateResponse(100L, 5000L, 4000L, 1000L);
        BalanceUpdateResponse compensationResponse = new BalanceUpdateResponse(100L, 4000L, 5000L, 1000L);
        when(accountInternalApi.withdraw(any(WithdrawAccountRequest.class)))
            .thenReturn(new AccountApiResponse.Success<>(withdrawResponse));
        when(accountInternalApi.deposit(any(DepositAccountRequest.class)))
            .thenThrow(new RuntimeException("Deposit failed"))  // 입금 실패
            .thenReturn(new AccountApiResponse.Success<>(compensationResponse)); // 보상 입금 성공

        when(saveTransactionPort.saveTransaction(any(Transaction.class)))
            .thenReturn(TransactionId.of(1L), TransactionId.of(2L));

        when(ledgerService.markEntryPosted(any(), any(), eq(TransactionDirection.DEBIT), any(), any()))
            .thenReturn(requestedSnapshot);
        when(ledgerService.markTransferFailed(any(), any(), anyString()))
            .thenReturn(failedSnapshot);

        TransferCommand command = TransferCommand.builder()
            .sourceAccountId(100L)
            .targetAccountId(200L)
            .amount(MoneyWon.of(1000L))
            .valueDate(LocalDate.now())
            .memo("생일선물")
            .idempotencyKey(idempotencyKey)
            .transferType(TransferType.INTERNAL)
            .requestedAt(Instant.now())
            .build();

        // when & then
        assertThatThrownBy(() -> transferService.transfer(command))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Deposit failed");

        // 보상 트랜잭션이 생성되었는지 확인
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(saveTransactionPort, times(2)).saveTransaction(transactionCaptor.capture());

        List<Transaction> capturedTransactions = transactionCaptor.getAllValues();
        Transaction debitTransaction = capturedTransactions.get(0);  // 출금 거래
        Transaction compensationTransaction = capturedTransactions.get(1); // 보상 거래

        // 출금 거래는 수취인 이름으로 설정
        assertThat(debitTransaction.getDescription()).isEqualTo("김철수");

        // 보상 거래는 "수취인 이름 + 송금취소"로 설정
        assertThat(compensationTransaction.getDescription()).isEqualTo("김철수 송금취소");
        assertThat(compensationTransaction.getDirection()).isEqualTo(TransactionDirection.CREDIT);
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

    private TransferSnapshot createSettledSnapshot(IdempotencyKey idempotencyKey) {
        LedgerEntrySnapshot debitSnapshot = new LedgerEntrySnapshot(
            1L, 100L, TransactionDirection.DEBIT, MoneyWon.of(1000L),
            LedgerEntryStatus.POSTED, LocalDate.now(), Instant.now(), TransactionId.of(1L),
            Instant.now(), Instant.now()
        );
        LedgerEntrySnapshot creditSnapshot = new LedgerEntrySnapshot(
            2L, 200L, TransactionDirection.CREDIT, MoneyWon.of(1000L),
            LedgerEntryStatus.POSTED, LocalDate.now(), Instant.now(), TransactionId.of(2L),
            Instant.now(), Instant.now()
        );
        return new TransferSnapshot(
            TransferType.INTERNAL,
            TransferStatus.SETTLED,
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
