package saviing.game.pet.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.pet.domain.model.aggregate.Pet;
import saviing.game.pet.domain.model.vo.Affection;
import saviing.game.pet.domain.model.vo.Energy;
import saviing.game.pet.domain.model.vo.Experience;
import saviing.game.pet.domain.model.vo.PetLevel;
import saviing.game.pet.domain.model.vo.PetName;
import saviing.game.pet.infrastructure.persistence.entity.PetEntity;

/**
 * Pet Entity ↔ Domain 간 변환 매퍼
 */
@Component
public class PetEntityMapper {

    /**
     * Entity → Domain 변환
     */
    public Pet toDomain(PetEntity entity) {
        if (entity == null) {
            return null;
        }

        return Pet.builder()
            .inventoryItemId(InventoryItemId.of(entity.getInventoryItemId()))
            .level(PetLevel.of(entity.getLevel()))
            .experience(Experience.of(entity.getExperience()))
            .affection(Affection.of(entity.getAffection()))
            .energy(Energy.of(entity.getEnergy()))
            .petName(PetName.of(entity.getPetName()))
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    /**
     * Domain → Entity 변환
     */
    public PetEntity toEntity(Pet domain) {
        if (domain == null) {
            return null;
        }

        return PetEntity.builder()
            .inventoryItemId(domain.getInventoryItemId().value())
            .level(domain.getLevel().value())
            .experience(domain.getExperience().value())
            .affection(domain.getAffection().value())
            .energy(domain.getEnergy().value())
            .petName(domain.getPetName().value())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    /**
     * Domain의 변경사항을 기존 Entity에 적용
     * JPA의 더티 체킹을 활용한 업데이트용
     */
    public void updateEntity(PetEntity entity, Pet domain) {
        if (entity == null || domain == null) {
            return;
        }

        entity.updatePet(
            domain.getLevel().value(),
            domain.getExperience().value(),
            domain.getAffection().value(),
            domain.getEnergy().value(),
            domain.getPetName().value()
        );
    }
}