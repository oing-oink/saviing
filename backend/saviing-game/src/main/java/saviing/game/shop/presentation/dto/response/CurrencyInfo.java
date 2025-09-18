package saviing.game.shop.presentation.dto.response;

import lombok.Builder;

/**
 * 구매 후 통화 정보 DTO입니다.
 * 사용한 결제 수단과 구매 후 잔액을 담습니다.
 *
 * @param coinType 사용한 결제 수단 (COIN 또는 FISH_COIN)
 * @param balance 구매 후 잔액
 */
@Builder
public record CurrencyInfo(
    String coinType,
    Integer balance
) {
}