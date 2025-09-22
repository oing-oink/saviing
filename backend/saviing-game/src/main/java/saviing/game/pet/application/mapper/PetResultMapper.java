package saviing.game.pet.application.mapper;

import org.springframework.stereotype.Component;
import saviing.game.pet.application.dto.result.PetResult;
import saviing.game.pet.domain.model.aggregate.Pet;

/**
 * 펫 도메인 객체를 애플리케이션 결과 DTO로 변환하는 매퍼
 */
@Component
public class PetResultMapper {

    /**
     * Pet 도메인 객체를 PetResult DTO로 변환합니다.
     *
     * @param pet 펫 도메인 객체
     * @return PetResult DTO
     */
    public PetResult toResult(Pet pet) {
        if (pet == null) {
            return null;
        }

        return PetResult.builder()
            .inventoryItemId(pet.getInventoryItemId())
            .level(pet.getLevel().value())
            .experience(pet.getExperience().value())
            .affection(pet.getAffection().value())
            .energy(pet.getEnergy().value())
            .petName(pet.getPetName().value())
            .createdAt(pet.getCreatedAt())
            .updatedAt(pet.getUpdatedAt())
            .build();
    }
}