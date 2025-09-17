package saviing.bank.transaction.domain.service;

import java.time.Instant;
import java.time.LocalDate;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.TransferStatus;
import saviing.bank.transaction.domain.model.TransferType;
import saviing.bank.transaction.domain.model.ledger.LedgerPairSnapshot;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.bank.transaction.domain.vo.TransferId;

/**
 * LedgerPair 애그리거트 조작을 위한 도메인 서비스 인터페이스.
 */
public interface LedgerService {

    /**
     * 송금 요청에 대해 LedgerPair를 조회하거나 새로 생성한다.
     */
    LedgerPairSnapshot initializeTransfer(
        TransferId transferId,
        Long sourceAccountId,
        Long targetAccountId,
        MoneyWon amount,
        LocalDate valueDate,
        TransferType transferType,
        IdempotencyKey idempotencyKey
    );

    /**
     * 출금/입금 엔트리가 POSTED 상태가 되었음을 기록한다.
     */
    LedgerPairSnapshot markEntryPosted(
        TransferId transferId,
        TransactionDirection direction,
        TransactionId transactionId,
        Instant postedAt
    );

    /**
     * 송금이 실패했음을 기록한다.
     */
    LedgerPairSnapshot markTransferFailed(TransferId transferId, String reason);

    /**
     * 송금이 성공적으로 정산되었음을 기록한다.
     */
    LedgerPairSnapshot markTransferSettled(TransferId transferId, Instant settledAt);

    /**
     * 현재 송금 상태를 조회한다.
     */
    TransferStatus getStatus(TransferId transferId);
}
