package saviing.bank.transaction.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import saviing.bank.transaction.adapter.out.persistence.entity.ledger.LedgerPairJpaEntity;
import saviing.bank.transaction.adapter.out.persistence.mapper.LedgerMapper;
import saviing.bank.transaction.adapter.out.persistence.repository.ledger.LedgerPairJpaRepository;
import saviing.bank.transaction.application.port.out.LedgerPersistencePort;
import saviing.bank.transaction.domain.model.ledger.LedgerPair;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.exception.LedgerNotFoundException;

import java.util.Map;

/**
 * LedgerPersistencePort의 JPA 어댑터 구현.
 * LedgerPair 애그리거트를 엔티티로 변환해 영속화하고, 필요한 경우 잠금 조회를 수행한다.
 */
@Component
@RequiredArgsConstructor
@Transactional
public class LedgerPersistenceAdapter implements LedgerPersistencePort {

    private final LedgerPairJpaRepository ledgerPairJpaRepository;
    private final LedgerMapper ledgerMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<LedgerPair> findByIdempotencyKey(IdempotencyKey idempotencyKey) {
        if (idempotencyKey == null) {
            return Optional.empty();
        }
        return ledgerPairJpaRepository.findByIdempotencyKey(idempotencyKey.value())
            .map(ledgerMapper::toDomain);
    }

    @Override
    public LedgerPair save(LedgerPair ledgerPair) {
        LedgerPairJpaEntity entity = ledgerMapper.toEntity(ledgerPair);
        LedgerPairJpaEntity saved = ledgerPairJpaRepository.save(entity);
        ledgerPairJpaRepository.flush();
        return ledgerMapper.toDomain(saved);
    }

    @Override
    public LedgerPair saveAndFlush(LedgerPair ledgerPair) {
        LedgerPairJpaEntity entity = ledgerPairJpaRepository.lockByIdempotencyKey(ledgerPair.getIdempotencyKey().value())
            .orElseThrow(() -> new LedgerNotFoundException(
                Map.of("idempotencyKey", ledgerPair.getIdempotencyKey().value())
            ));
        ledgerMapper.updateEntity(ledgerPair, entity);
        LedgerPairJpaEntity saved = ledgerPairJpaRepository.save(entity);
        ledgerPairJpaRepository.flush();
        return ledgerMapper.toDomain(saved);
    }
}
