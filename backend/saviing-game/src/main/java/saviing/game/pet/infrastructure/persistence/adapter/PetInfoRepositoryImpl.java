package saviing.game.pet.infrastructure.persistence.adapter;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.pet.domain.model.aggregate.PetInfo;
import saviing.game.pet.domain.repository.PetInfoRepository;
import saviing.game.pet.infrastructure.persistence.entity.PetInfoEntity;
import saviing.game.pet.infrastructure.persistence.mapper.PetInfoEntityMapper;
import saviing.game.pet.infrastructure.persistence.repository.PetInfoJpaRepository;

/**
 * PetInfo Repository 구현체
 * Domain Repository Interface를 Infrastructure 계층에서 구현합니다.
 */
@Repository
@RequiredArgsConstructor
public class PetInfoRepositoryImpl implements PetInfoRepository {

    private final PetInfoJpaRepository petInfoJpaRepository;
    private final PetInfoEntityMapper petInfoEntityMapper;

    @Override
    public Optional<PetInfo> findById(InventoryItemId inventoryItemId) {
        return petInfoJpaRepository.findByInventoryItemId(inventoryItemId.value())
            .map(petInfoEntityMapper::toDomain);
    }

    @Override
    public boolean existsById(InventoryItemId inventoryItemId) {
        return petInfoJpaRepository.existsByInventoryItemId(inventoryItemId.value());
    }

    @Override
    public PetInfo save(PetInfo petInfo) {
        PetInfoEntity entity = petInfoEntityMapper.toEntity(petInfo);
        PetInfoEntity savedEntity = petInfoJpaRepository.save(entity);
        return petInfoEntityMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(InventoryItemId inventoryItemId) {
        petInfoJpaRepository.deleteById(inventoryItemId.value());
    }
}