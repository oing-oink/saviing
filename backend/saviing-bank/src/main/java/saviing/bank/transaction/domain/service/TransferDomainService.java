package saviing.bank.transaction.domain.service;

import java.time.LocalDate;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.vo.AccountSnapshot;
import saviing.bank.transaction.domain.model.transfer.TransferType;
import saviing.bank.transaction.domain.vo.TransferSnapshot;
import saviing.bank.transaction.domain.vo.IdempotencyKey;

/**
 * 송금 도메인 규칙을 집중 관리하는 서비스 인터페이스.
 */
public interface TransferDomainService {

    /**
     * 송금 수행 전 계좌 상태, 금액, 가치일을 검증한다.
     */
    void validatePreconditions(
        AccountSnapshot sourceAccount,
        AccountSnapshot targetAccount,
        MoneyWon amount,
        LocalDate valueDate,
        TransferType transferType
    );

    /**
     * 멱등 키를 검증한다.
     */
    void ensureIdempotency(IdempotencyKey idempotencyKey);

    /**
     * 송금이 성공적으로 정산되었을 때 후속 처리를 수행한다.
     */
    void onTransferSettled(IdempotencyKey idempotencyKey, TransferSnapshot ledgerPair);

    /**
     * 송금 실패 시 후속 처리를 수행한다.
     */
    void onTransferFailed(IdempotencyKey idempotencyKey, TransferSnapshot ledgerPair, Throwable cause);
}
