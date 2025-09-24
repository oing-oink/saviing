package saviing.game.pet.application.dto.result;

import lombok.Builder;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

import java.time.LocalDateTime;

/**
 * 펫 조회 결과 DTO
 */
@Builder
public record PetResult(
    InventoryItemId inventoryItemId,
    Long petId,
    Long itemId,
    int level,
    int experience,
    int requiredExp,
    int affection,
    int energy,
    String petName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}