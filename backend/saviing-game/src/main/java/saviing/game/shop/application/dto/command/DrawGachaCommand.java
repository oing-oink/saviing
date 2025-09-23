package saviing.game.shop.application.dto.command;

import lombok.Builder;
import saviing.game.shop.domain.model.vo.PaymentMethod;

/**
 * 가챠 뽑기 명령 DTO
 *
 * @param characterId 캐릭터 ID
 * @param gachaPoolId 가챠풀 ID
 * @param paymentMethod 결제 수단
 */
@Builder
public record DrawGachaCommand(
    Long characterId,
    Long gachaPoolId,
    PaymentMethod paymentMethod
) {

    /**
     * 명령의 유효성을 검증합니다.
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
        if (paymentMethod == null) {
            throw new IllegalArgumentException("결제 수단은 필수입니다");
        }
    }
}