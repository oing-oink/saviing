package saviing.game.item.application.dto.command;

import lombok.Builder;
import saviing.game.item.domain.model.enums.ItemType;
import saviing.game.item.domain.model.enums.Rarity;
import saviing.game.item.domain.model.enums.category.Category;

/**
 * 아이템 등록 명령 DTO
 */
@Builder
public record RegisterItemCommand(
    String itemName,
    String itemDescription,
    ItemType itemType,
    Category itemCategory,
    Rarity rarity,
    Integer xLength,
    Integer yLength,
    Integer coin,
    Integer fishCoin,
    String imageUrl
) {
}