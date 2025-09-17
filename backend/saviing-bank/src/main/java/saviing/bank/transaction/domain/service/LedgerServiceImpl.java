package saviing.bank.transaction.domain.service;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.application.port.out.LedgerPersistencePort;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.TransferStatus;
import saviing.bank.transaction.domain.model.TransferType;
import saviing.bank.transaction.domain.model.ledger.LedgerPair;
import saviing.bank.transaction.domain.model.ledger.LedgerPairSnapshot;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.bank.transaction.domain.vo.TransferId;
import saviing.bank.transaction.exception.InvalidLedgerStateException;
import saviing.bank.transaction.exception.LedgerNotFoundException;

import java.util.Map;

/**
 * LedgerPair 애그리거트의 상태 변이를 담당하는 도메인 서비스.
 * 저장소 접근은 포트를 통해 숨기고 송금 상태 전이를 일관되게 처리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LedgerServiceImpl implements LedgerService {

    private final LedgerPersistencePort ledgerPersistencePort;

    @Override
    /** {@inheritDoc} */
    public LedgerPairSnapshot initializeTransfer(
        TransferId transferId,
        Long sourceAccountId,
        Long targetAccountId,
        MoneyWon amount,
        LocalDate valueDate,
        TransferType transferType,
        IdempotencyKey idempotencyKey
    ) {
        LedgerPair ledgerPair = null;

        if (idempotencyKey != null) {
            ledgerPair = ledgerPersistencePort.findByIdempotencyKey(idempotencyKey)
                .orElse(null);
        }

        if (ledgerPair == null) {
            ledgerPair = ledgerPersistencePort.findByTransferId(transferId)
                .orElse(null);
        }

        if (ledgerPair == null) {
            // 새 송금의 첫 호출이면 LedgerPair를 생성한다.
            ledgerPair = createNewPair(
                transferId,
                sourceAccountId,
                targetAccountId,
                amount,
                valueDate,
                transferType,
                idempotencyKey
            );
        }

        return ledgerPair.toSnapshot();
    }

    /**
     * 새 LedgerPair를 생성하고 저장한다.
     */
    private LedgerPair createNewPair(
        TransferId transferId,
        Long sourceAccountId,
        Long targetAccountId,
        MoneyWon amount,
        LocalDate valueDate,
        TransferType transferType,
        IdempotencyKey idempotencyKey
    ) {
        LedgerPair ledgerPair = LedgerPair.create(
            transferId,
            sourceAccountId,
            targetAccountId,
            amount,
            valueDate,
            transferType,
            idempotencyKey,
            Instant.now()
        );
        return ledgerPersistencePort.save(ledgerPair);
    }

    @Override
    public LedgerPairSnapshot markEntryPosted(
        TransferId transferId,
        TransactionDirection direction,
        TransactionId transactionId,
        Instant postedAt
    ) {
        LedgerPair ledgerPair = ledgerPersistencePort.findByTransferId(transferId)
            .orElseThrow(() -> new LedgerNotFoundException(Map.of("transferId", transferId.value())));

        if (ledgerPair.getStatus() == TransferStatus.FAILED || ledgerPair.getStatus() == TransferStatus.VOID) {
            throw new InvalidLedgerStateException("Cannot post entry for transfer in status " + ledgerPair.getStatus());
        }

        ledgerPair.markEntryPosted(direction, transactionId, postedAt);
        LedgerPair updated = ledgerPersistencePort.saveAndFlush(ledgerPair);
        return updated.toSnapshot();
    }

    @Override
    public LedgerPairSnapshot markTransferFailed(TransferId transferId, String reason) {
        LedgerPair ledgerPair = ledgerPersistencePort.findByTransferId(transferId)
            .orElseThrow(() -> new LedgerNotFoundException(Map.of("transferId", transferId.value())));
        ledgerPair.markFailed(reason, Instant.now());
        LedgerPair updated = ledgerPersistencePort.saveAndFlush(ledgerPair);
        return updated.toSnapshot();
    }

    @Override
    public LedgerPairSnapshot markTransferSettled(TransferId transferId, Instant settledAt) {
        LedgerPair ledgerPair = ledgerPersistencePort.findByTransferId(transferId)
            .orElseThrow(() -> new LedgerNotFoundException(Map.of("transferId", transferId.value())));
        ledgerPair.markSettled(settledAt);
        LedgerPair updated = ledgerPersistencePort.saveAndFlush(ledgerPair);
        return updated.toSnapshot();
    }

    @Override
    @Transactional(readOnly = true)
    public TransferStatus getStatus(TransferId transferId) {
        return ledgerPersistencePort.findByTransferId(transferId)
            .map(LedgerPair::getStatus)
            .orElse(TransferStatus.REQUESTED);
    }
}
