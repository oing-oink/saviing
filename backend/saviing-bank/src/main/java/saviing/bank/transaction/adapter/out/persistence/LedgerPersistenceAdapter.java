package saviing.bank.transaction.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import saviing.bank.transaction.adapter.out.persistence.entity.ledger.TransferJpaEntity;
import saviing.bank.transaction.adapter.out.persistence.mapper.LedgerMapper;
import saviing.bank.transaction.adapter.out.persistence.repository.ledger.TransferJpaRepository;
import saviing.bank.transaction.application.port.out.LedgerPersistencePort;
import saviing.bank.transaction.domain.model.transfer.Transfer;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.exception.LedgerNotFoundException;

import java.util.Map;

/**
 * LedgerPersistencePort의 JPA 어댑터 구현.
 * Transfer 애그리거트를 엔티티로 변환해 영속화하고, 필요한 경우 잠금 조회를 수행한다.
 */
@Component
@RequiredArgsConstructor
@Transactional
public class LedgerPersistenceAdapter implements LedgerPersistencePort {

    private final TransferJpaRepository ledgerPairJpaRepository;
    private final LedgerMapper ledgerMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Transfer> findBySourceAccountIdAndIdempotencyKey(Long sourceAccountId, IdempotencyKey idempotencyKey) {
        if (sourceAccountId == null || idempotencyKey == null) {
            return Optional.empty();
        }
        return ledgerPairJpaRepository.findBySourceAccountIdAndIdempotencyKey(sourceAccountId, idempotencyKey.value())
            .map(ledgerMapper::toDomain);
    }

    @Override
    public Transfer save(Transfer ledgerPair) {
        TransferJpaEntity entity = ledgerMapper.toEntity(ledgerPair);
        TransferJpaEntity saved = ledgerPairJpaRepository.save(entity);
        ledgerPairJpaRepository.flush();
        return ledgerMapper.toDomain(saved);
    }

    @Override
    public Transfer saveAndFlush(Transfer ledgerPair) {
        IdempotencyKey idempotencyKey = ledgerPair.getIdempotencyKey();
        Long sourceAccountId = ledgerPair.getSourceAccountId();

        if (idempotencyKey == null || sourceAccountId == null) {
            throw new LedgerNotFoundException(
                Map.of(
                    "idempotencyKey", idempotencyKey != null ? idempotencyKey.value() : "null",
                    "sourceAccountId", String.valueOf(sourceAccountId)
                )
            );
        }

        TransferJpaEntity entity = ledgerPairJpaRepository.lockBySourceAccountIdAndIdempotencyKey(
                sourceAccountId,
                idempotencyKey.value()
            )
            .orElseThrow(() -> new LedgerNotFoundException(
                Map.of(
                    "idempotencyKey", idempotencyKey.value(),
                    "sourceAccountId", String.valueOf(sourceAccountId)
                )
            ));
        ledgerMapper.updateEntity(ledgerPair, entity);
        TransferJpaEntity saved = ledgerPairJpaRepository.save(entity);
        ledgerPairJpaRepository.flush();
        return ledgerMapper.toDomain(saved);
    }
}
