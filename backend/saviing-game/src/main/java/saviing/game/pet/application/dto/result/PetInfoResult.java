package saviing.game.pet.application.dto.result;

import lombok.Builder;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

import java.time.LocalDateTime;

/**
 * 펫 정보 조회 결과 DTO
 */
@Builder
public record PetInfoResult(
    InventoryItemId inventoryItemId,
    int level,
    int experience,
    int affection,
    int energy,
    String petName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}