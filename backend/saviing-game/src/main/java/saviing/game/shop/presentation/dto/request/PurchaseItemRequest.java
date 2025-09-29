package saviing.game.shop.presentation.dto.request;

import lombok.Builder;

/**
 * 아이템 구매 요청 DTO
 */
@Builder
public record PurchaseItemRequest(
    Long characterId,
    Long itemId,
    String paymentMethod,
    Integer count
) {
    public PurchaseItemRequest {
        // count가 null이거나 0 이하면 기본값 1로 설정
        if (count == null || count <= 0) {
            count = 1;
        }
    }
}