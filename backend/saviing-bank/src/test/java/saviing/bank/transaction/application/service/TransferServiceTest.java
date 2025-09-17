package saviing.bank.transaction.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.context.ApplicationEventPublisher;

import saviing.bank.account.api.AccountInternalApi;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.application.port.in.command.TransferCommand;
import saviing.bank.transaction.application.port.in.result.TransferResult;
import saviing.bank.transaction.application.port.out.LoadTransactionPort;
import saviing.bank.transaction.application.port.out.SaveTransactionPort;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.TransferStatus;
import saviing.bank.transaction.domain.model.TransferType;
import saviing.bank.transaction.domain.model.ledger.LedgerEntrySnapshot;
import saviing.bank.transaction.domain.model.ledger.LedgerEntryStatus;
import saviing.bank.transaction.domain.model.ledger.LedgerPairSnapshot;
import saviing.bank.transaction.domain.service.LedgerService;
import saviing.bank.transaction.domain.service.TransferDomainService;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.bank.transaction.domain.vo.TransferId;

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
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transferReturnsExistingResultWhenLedgerAlreadySettled() {
        // given
        IdempotencyKey idempotencyKey = IdempotencyKey.of("transfer-1");
        TransferId transferId = TransferId.of(idempotencyKey.value());
        LedgerEntrySnapshot debitSnapshot = new LedgerEntrySnapshot(
            1L,
            100L,
            TransactionDirection.DEBIT,
            MoneyWon.of(1000L),
            LedgerEntryStatus.POSTED,
            LocalDate.now(),
            Instant.now(),
            null,
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
            null,
            TransactionId.of(11L),
            Instant.now(),
            Instant.now()
        );
        LedgerPairSnapshot settledSnapshot = new LedgerPairSnapshot(
            transferId,
            TransferType.INTERNAL,
            TransferStatus.SETTLED,
            idempotencyKey,
            List.of(debitSnapshot, creditSnapshot),
            Instant.now().minusSeconds(5),
            Instant.now(),
            null
        );

        when(ledgerService.initializeTransfer(
            eq(transferId),
            any(Long.class),
            any(Long.class),
            any(MoneyWon.class),
            any(LocalDate.class),
            any(TransferType.class),
            eq(idempotencyKey)
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
        assertThat(result.transferId()).isEqualTo(transferId);
        assertThat(result.status()).isEqualTo(TransferStatus.SETTLED);
        assertThat(result.debitTransactionId()).isEqualTo(TransactionId.of(10L));
        assertThat(result.creditTransactionId()).isEqualTo(TransactionId.of(11L));
        verifyNoInteractions(accountInternalApi, saveTransactionPort);
    }
}
