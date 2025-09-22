package saviing.game.room.domain.model.dto;

import java.util.Objects;

import saviing.game.room.domain.model.aggregate.Category;

/**
 * 배치 요청을 나타내는 임시 DTO
 * 도메인 서비스에서 검증 전 단계의 데이터를 담는 용도
 */
public record PlacementDraft(
    Long inventoryItemId,
    int positionX,
    int positionY,
    int xLength,
    int yLength,
    Category category
) {

    public PlacementDraft {
        Objects.requireNonNull(inventoryItemId, "inventoryItemId");
        Objects.requireNonNull(category, "category");

        if (inventoryItemId <= 0) {
            throw new IllegalArgumentException("InventoryItemId must be positive");
        }
        if (positionX < 0 || positionY < 0) {
            throw new IllegalArgumentException("Position coordinates must be non-negative");
        }
        if (xLength < 1 || yLength < 1) {
            throw new IllegalArgumentException("ItemSize dimensions must be positive");
        }
    }

    public boolean isPet() {
        return category == Category.PET;
    }
}
