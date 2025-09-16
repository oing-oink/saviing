package saviing.game.item.presentation.dto.request;

import lombok.Builder;

/**
 * 아이템 생성 요청 DTO
 */
@Builder
public record CreateItemRequest(
    String itemName,
    String itemDescription,
    String itemType,
    String itemCategory,
    String rarity,
    Integer xLength,
    Integer yLength,
    Integer coin,
    Integer fishCoin,
    String imageUrl
) {
}