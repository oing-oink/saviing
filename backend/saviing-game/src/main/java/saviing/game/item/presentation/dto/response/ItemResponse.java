package saviing.game.item.presentation.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 아이템 API 응답 DTO
 */
@Builder
public record ItemResponse(
    Long itemId,
    String itemName,
    String itemDescription,
    String itemType,
    String itemCategory,
    String rarity,
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