package saviing.game.shop.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import saviing.game.shop.domain.model.aggregate.PurchaseRecord;
import saviing.game.shop.domain.repository.PurchaseRecordRepository;
import saviing.game.shop.infrastructure.persistence.mapper.PurchaseRecordEntityMapper;

import java.util.List;
import java.util.Optional;

/**
 * 구매 기록 저장소 구현체입니다.
 */
@Repository
@RequiredArgsConstructor
public class PurchaseRecordRepositoryImpl implements PurchaseRecordRepository {

    private final PurchaseRecordJpaRepository jpaRepository;
    private final PurchaseRecordEntityMapper entityMapper;

    @Override
    public PurchaseRecord save(PurchaseRecord purchaseRecord) {
        var entity = entityMapper.toEntity(purchaseRecord);
        var savedEntity = jpaRepository.save(entity);
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PurchaseRecord> findById(Long purchaseId) {
        return jpaRepository.findById(purchaseId)
            .map(entityMapper::toDomain);
    }

    @Override
    public List<PurchaseRecord> findByCharacterId(Long characterId) {
        return jpaRepository.findByCharacterIdOrderByCompletedAtDesc(characterId)
            .stream()
            .map(entityMapper::toDomain)
            .toList();
    }
}