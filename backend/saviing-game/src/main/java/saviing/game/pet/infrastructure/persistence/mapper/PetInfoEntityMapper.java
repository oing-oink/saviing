package saviing.game.pet.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.pet.domain.model.aggregate.PetInfo;
import saviing.game.pet.domain.model.vo.Affection;
import saviing.game.pet.domain.model.vo.Energy;
import saviing.game.pet.domain.model.vo.Experience;
import saviing.game.pet.domain.model.vo.PetLevel;
import saviing.game.pet.infrastructure.persistence.entity.PetInfoEntity;

/**
 * PetInfo Entity ↔ Domain 간 변환 매퍼
 */
@Component
public class PetInfoEntityMapper {

    /**
     * Entity → Domain 변환
     */
    public PetInfo toDomain(PetInfoEntity entity) {
        if (entity == null) {
            return null;
        }

        return PetInfo.builder()
            .inventoryItemId(InventoryItemId.of(entity.getInventoryItemId()))
            .level(PetLevel.of(entity.getLevel()))
            .experience(Experience.of(entity.getExperience()))
            .affection(Affection.of(entity.getAffection()))
            .energy(Energy.of(entity.getEnergy()))
            .petName(entity.getPetName())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    /**
     * Domain → Entity 변환
     */
    public PetInfoEntity toEntity(PetInfo domain) {
        if (domain == null) {
            return null;
        }

        return PetInfoEntity.builder()
            .inventoryItemId(domain.getInventoryItemId().value())
            .level(domain.getLevel().value())
            .experience(domain.getExperience().value())
            .affection(domain.getAffection().value())
            .energy(domain.getEnergy().value())
            .petName(domain.getPetName())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    /**
     * Domain의 변경사항을 기존 Entity에 적용
     * JPA의 더티 체킹을 활용한 업데이트용
     */
    public void updateEntity(PetInfoEntity entity, PetInfo domain) {
        if (entity == null || domain == null) {
            return;
        }

        entity.updateEntity(
            domain.getLevel().value(),
            domain.getExperience().value(),
            domain.getAffection().value(),
            domain.getEnergy().value(),
            domain.getPetName()
        );
    }
}