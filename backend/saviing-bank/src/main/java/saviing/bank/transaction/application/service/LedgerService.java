package saviing.bank.transaction.application.service;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.application.port.out.LedgerPersistencePort;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.transfer.TransferStatus;
import saviing.bank.transaction.domain.model.transfer.TransferType;
import saviing.bank.transaction.domain.model.transfer.Transfer;
import saviing.bank.transaction.domain.vo.TransferSnapshot;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.bank.transaction.exception.InvalidLedgerStateException;
import saviing.bank.transaction.exception.LedgerNotFoundException;

import java.util.Map;

/**
 * Transfer 애그리거트의 상태 변이를 담당하는 애플리케이션 서비스.
 * 저장소 접근은 포트를 통해 숨기고 송금 상태 전이를 일관되게 처리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LedgerService {

    private final LedgerPersistencePort ledgerPersistencePort;

    /**
     * 송금 요청에 대해 Transfer를 조회하거나 새로 생성한다.
     */
    public TransferSnapshot initializeTransfer(
        IdempotencyKey idempotencyKey,
        Long sourceAccountId,
        Long targetAccountId,
        MoneyWon amount,
        LocalDate valueDate,
        TransferType transferType
    ) {
        Transfer ledgerPair = null;

        if (idempotencyKey != null) {
            ledgerPair = ledgerPersistencePort.findByIdempotencyKey(idempotencyKey)
                .orElse(null);
        }

        if (ledgerPair == null) {
            // 새 송금의 첫 호출이면 Transfer를 생성한다.
            ledgerPair = createNewPair(
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
     * 새 Transfer를 생성하고 저장한다.
     */
    private Transfer createNewPair(
        Long sourceAccountId,
        Long targetAccountId,
        MoneyWon amount,
        LocalDate valueDate,
        TransferType transferType,
        IdempotencyKey idempotencyKey
    ) {
        Transfer ledgerPair = Transfer.create(
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

    /**
     * 출금/입금 엔트리가 POSTED 상태가 되었음을 기록한다.
     */
    public TransferSnapshot markEntryPosted(
        IdempotencyKey idempotencyKey,
        TransactionDirection direction,
        TransactionId transactionId,
        Instant postedAt
    ) {
        Transfer ledgerPair = ledgerPersistencePort.findByIdempotencyKey(idempotencyKey)
            .orElseThrow(() -> new LedgerNotFoundException(Map.of("idempotencyKey", idempotencyKey.value())));

        if (ledgerPair.getStatus() == TransferStatus.FAILED || ledgerPair.getStatus() == TransferStatus.VOID) {
            throw new InvalidLedgerStateException("Cannot post entry for transfer in status " + ledgerPair.getStatus());
        }

        ledgerPair.markEntryPosted(direction, transactionId, postedAt);
        Transfer updated = ledgerPersistencePort.saveAndFlush(ledgerPair);
        return updated.toSnapshot();
    }

    /**
     * 송금이 실패했음을 기록한다.
     */
    public TransferSnapshot markTransferFailed(IdempotencyKey idempotencyKey, String reason) {
        Transfer ledgerPair = ledgerPersistencePort.findByIdempotencyKey(idempotencyKey)
            .orElseThrow(() -> new LedgerNotFoundException(Map.of("idempotencyKey", idempotencyKey.value())));
        ledgerPair.markFailed(reason, Instant.now());
        Transfer updated = ledgerPersistencePort.saveAndFlush(ledgerPair);
        return updated.toSnapshot();
    }

    /**
     * 송금이 성공적으로 정산되었음을 기록한다.
     */
    public TransferSnapshot markTransferSettled(IdempotencyKey idempotencyKey, Instant settledAt) {
        Transfer ledgerPair = ledgerPersistencePort.findByIdempotencyKey(idempotencyKey)
            .orElseThrow(() -> new LedgerNotFoundException(Map.of("idempotencyKey", idempotencyKey.value())));
        ledgerPair.markSettled(settledAt);
        Transfer updated = ledgerPersistencePort.saveAndFlush(ledgerPair);
        return updated.toSnapshot();
    }

    /**
     * 현재 송금 상태를 조회한다.
     */
    @Transactional(readOnly = true)
    public TransferStatus getStatus(IdempotencyKey idempotencyKey) {
        return ledgerPersistencePort.findByIdempotencyKey(idempotencyKey)
            .map(Transfer::getStatus)
            .orElse(TransferStatus.REQUESTED);
    }
}
