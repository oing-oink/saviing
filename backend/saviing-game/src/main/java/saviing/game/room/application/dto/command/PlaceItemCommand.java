package saviing.game.room.application.dto.command;

import java.util.Objects;

import saviing.game.room.domain.model.aggregate.Category;

/**
 * 아이템 배치 명령을 나타내는 DTO
 * 애플리케이션 서비스에서 외부 요청을 도메인 객체로 변환하기 전 단계의 데이터를 담는 용도
 */
public record PlaceItemCommand(
    Long inventoryItemId,
    int positionX,
    int positionY,
    int xLength,
    int yLength,
    Category category
) {

    public PlaceItemCommand {
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