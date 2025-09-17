package saviing.bank.transaction.application.port.out;

import java.util.Optional;

import saviing.bank.transaction.domain.model.ledger.LedgerPair;
import saviing.bank.transaction.domain.vo.IdempotencyKey;

/**
 * LedgerPair 애그리거트 저장소와 상호작용하기 위한 아웃바운드 포트.
 */
public interface LedgerPersistencePort {

    /**
     * 멱등 키로 LedgerPair를 조회한다.
     */
    Optional<LedgerPair> findByIdempotencyKey(IdempotencyKey idempotencyKey);

    /**
     * LedgerPair를 저장한다.
     */
    LedgerPair save(LedgerPair ledgerPair);

    /**
     * LedgerPair를 업데이트하고 즉시 플러시한다.
     */
    LedgerPair saveAndFlush(LedgerPair ledgerPair);
}
