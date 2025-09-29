package saviing.game.shop.presentation.dto.response;

import lombok.Builder;
import saviing.game.item.presentation.dto.response.ItemResponse;

/**
 * 구매 응답 DTO입니다.
 * 구매 성공시 아이템 정보와 잔액 정보를 담습니다.
 *
 * @param item 구매한 아이템 전체 정보
 * @param currencies 결제 수단과 구매 후 잔액 정보
 */
@Builder
public record PurchaseResponse(
    ItemResponse item,
    CurrencyInfo currencies
) {
}