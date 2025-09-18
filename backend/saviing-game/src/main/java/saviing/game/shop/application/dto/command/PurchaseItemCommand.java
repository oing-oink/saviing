package saviing.game.shop.application.dto.command;

import lombok.Builder;

/**
 * 아이템 구매 명령 DTO입니다.
 * 구매 요청에 필요한 정보를 담습니다.
 *
 * @param characterId 캐릭터 ID
 * @param itemId 아이템 ID
 * @param paymentMethod 결제 수단 (COIN 또는 FISH_COIN)
 */
@Builder
public record PurchaseItemCommand(
    Long characterId,
    Long itemId,
    String paymentMethod
) {
    /**
     * 명령의 유효성을 검증합니다.
     *
     * @throws IllegalArgumentException 유효하지 않은 파라미터가 있는 경우
     */
    public void validate() {
        if (characterId == null || characterId <= 0) {
            throw new IllegalArgumentException("캐릭터 ID는 양수여야 합니다");
        }
        if (itemId == null || itemId <= 0) {
            throw new IllegalArgumentException("아이템 ID는 양수여야 합니다");
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("결제 수단은 필수입니다");
        }
        if (!"COIN".equals(paymentMethod) && !"FISH_COIN".equals(paymentMethod)) {
            throw new IllegalArgumentException("지원하지 않는 결제 수단입니다: " + paymentMethod);
        }
    }
}