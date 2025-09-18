package saviing.game.shop.presentation.dto.request;

import lombok.Builder;

/**
 * 아이템 구매 요청 DTO
 */
@Builder
public record PurchaseItemRequest(
    Long characterId,
    Long itemId,
    String paymentMethod
) {
}