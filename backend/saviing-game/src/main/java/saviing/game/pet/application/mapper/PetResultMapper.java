package saviing.game.pet.application.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import saviing.game.inventory.domain.repository.InventoryRepository;
import saviing.game.pet.application.dto.result.PetResult;
import saviing.game.pet.domain.model.aggregate.Pet;

/**
 * 펫 도메인 객체를 애플리케이션 결과 DTO로 변환하는 매퍼
 */
@Component
@RequiredArgsConstructor
public class PetResultMapper {

    private final InventoryRepository inventoryRepository;

    /**
     * Pet 도메인 객체를 PetResult DTO로 변환합니다.
     * 인벤토리에서 ItemId를 조회하여 함께 포함합니다.
     *
     * @param pet 펫 도메인 객체
     * @return PetResult DTO
     */
    public PetResult toResult(Pet pet) {
        if (pet == null) {
            return null;
        }

        // 인벤토리에서 ItemId 조회
        Long itemId = inventoryRepository.findItemIdByInventoryItemId(pet.getInventoryItemId())
            .orElseThrow(() -> new IllegalStateException("펫에 해당하는 인벤토리 아이템을 찾을 수 없습니다: " + pet.getInventoryItemId().value()));

        return PetResult.builder()
            .inventoryItemId(pet.getInventoryItemId())
            .petId(pet.getPetId().value())
            .itemId(itemId)
            .level(pet.getLevel().value())
            .experience(pet.getExperience().value())
            .requiredExp(pet.calculateRequiredExpForNextLevel())
            .affection(pet.getAffection().value())
            .energy(pet.getEnergy().value())
            .petName(pet.getPetName().value())
            .createdAt(pet.getCreatedAt())
            .updatedAt(pet.getUpdatedAt())
            .build();
    }
}