package saviing.game.item.presentation.dto.request;

import lombok.Builder;

/**
 * 아이템 수정 요청 DTO
 */
@Builder
public record UpdateItemRequest(
    String itemName,
    String itemDescription,
    String rarity,
    Integer coin,
    Integer fishCoin,
    String imageUrl
) {
}