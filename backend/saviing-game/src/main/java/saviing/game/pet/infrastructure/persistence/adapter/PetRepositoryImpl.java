package saviing.game.pet.infrastructure.persistence.adapter;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.pet.domain.model.aggregate.Pet;
import saviing.game.pet.domain.repository.PetRepository;
import saviing.game.pet.infrastructure.persistence.entity.PetEntity;
import saviing.game.pet.infrastructure.persistence.mapper.PetEntityMapper;
import saviing.game.pet.infrastructure.persistence.repository.PetJpaRepository;

/**
 * Pet Repository 구현체
 * Domain Repository Interface를 Infrastructure 계층에서 구현합니다.
 */
@Repository
@RequiredArgsConstructor
public class PetRepositoryImpl implements PetRepository {

    private final PetJpaRepository petJpaRepository;
    private final PetEntityMapper petEntityMapper;

    @Override
    public Optional<Pet> findById(InventoryItemId inventoryItemId) {
        return petJpaRepository.findByInventoryItemId(inventoryItemId.value())
            .map(petEntityMapper::toDomain);
    }

    @Override
    public boolean existsById(InventoryItemId inventoryItemId) {
        return petJpaRepository.existsByInventoryItemId(inventoryItemId.value());
    }

    @Override
    public Pet save(Pet pet) {
        PetEntity entity = petEntityMapper.toEntity(pet);
        PetEntity savedEntity = petJpaRepository.save(entity);
        return petEntityMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(InventoryItemId inventoryItemId) {
        petJpaRepository.deleteById(inventoryItemId.value());
    }
}