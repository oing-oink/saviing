package saviing.game.item.application.dto.result;

import lombok.Builder;
import saviing.game.item.domain.model.enums.ItemType;
import saviing.game.item.domain.model.enums.Rarity;
import saviing.game.item.domain.model.enums.category.Category;

import java.time.LocalDateTime;

/**
 * 아이템 조회 결과 DTO
 */
@Builder
public record ItemResult(
    Long itemId,
    String itemName,
    String itemDescription,
    ItemType itemType,
    Category itemCategory,
    Rarity rarity,
    Integer xLength,
    Integer yLength,
    Integer coin,
    Integer fishCoin,
    String imageUrl,
    boolean isAvailable,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}