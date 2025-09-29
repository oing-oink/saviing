package saviing.game.shop.presentation.dto.request;

import lombok.Builder;

/**
 * 가챠 뽑기 요청 DTO
 *
 * @param characterId 캐릭터 ID
 * @param gachaPoolId 가챠풀 ID
 * @param paymentMethod 결제 수단 ("COIN" 또는 "FISH_COIN")
 */
@Builder
public record GachaDrawRequest(
    Long characterId,
    Long gachaPoolId,
    String paymentMethod
) {

    /**
     * 요청의 유효성을 검증합니다.
     *
     * @throws IllegalArgumentException 유효하지 않은 값이 있는 경우
     */
    public void validate() {
        if (characterId == null || characterId <= 0) {
            throw new IllegalArgumentException("캐릭터 ID는 양수여야 합니다");
        }
        if (gachaPoolId == null || gachaPoolId <= 0) {
            throw new IllegalArgumentException("가챠풀 ID는 양수여야 합니다");
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("결제 수단은 필수입니다");
        }
        if (!isValidPaymentMethod(paymentMethod)) {
            throw new IllegalArgumentException("유효하지 않은 결제 수단입니다: " + paymentMethod);
        }
    }

    /**
     * 유효한 결제 수단인지 확인합니다.
     *
     * @param paymentMethod 결제 수단
     * @return 유효성 여부
     */
    private boolean isValidPaymentMethod(String paymentMethod) {
        return "COIN".equals(paymentMethod) || "FISH_COIN".equals(paymentMethod);
    }
}