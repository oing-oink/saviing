package saviing.bank.transaction.application.port.out;

import java.util.Optional;

import saviing.bank.transaction.domain.model.transfer.Transfer;
import saviing.bank.transaction.domain.vo.IdempotencyKey;

/**
 * Transfer 애그리거트 저장소와 상호작용하기 위한 아웃바운드 포트.
 */
public interface LedgerPersistencePort {

    /**
     * 출금 계좌와 멱등 키 조합으로 Transfer를 조회한다.
     */
    Optional<Transfer> findBySourceAccountIdAndIdempotencyKey(Long sourceAccountId, IdempotencyKey idempotencyKey);

    /**
     * Transfer를 저장한다.
     */
    Transfer save(Transfer ledgerPair);

    /**
     * Transfer를 업데이트하고 즉시 플러시한다.
     */
    Transfer saveAndFlush(Transfer ledgerPair);
}
