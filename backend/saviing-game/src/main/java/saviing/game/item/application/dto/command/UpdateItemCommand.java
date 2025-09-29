package saviing.game.item.application.dto.command;

import lombok.Builder;
import saviing.game.item.domain.model.enums.Rarity;

/**
 * 아이템 수정 명령 DTO
 */
@Builder
public record UpdateItemCommand(
    Long itemId,
    String itemName,
    String itemDescription,
    Rarity rarity,
    Integer xLength,
    Integer yLength,
    Integer coin,
    Integer fishCoin,
    String imageUrl
) {
}