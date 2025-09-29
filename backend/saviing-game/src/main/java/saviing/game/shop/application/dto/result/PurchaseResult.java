package saviing.game.shop.application.dto.result;

import lombok.Builder;
import saviing.game.character.application.dto.result.CharacterResult;
import saviing.game.item.application.dto.result.ItemResult;

/**
 * 구매 처리 결과를 담는 DTO입니다.
 * Application layer에서 Presentation layer로 데이터를 전달하기 위해 사용합니다.
 */
@Builder
public record PurchaseResult(
    ItemResult item,
    CharacterResult character,
    String paymentMethod
) {
    /**
     * PurchaseResult 생성 시 유효성 검증을 수행합니다.
     */
    public PurchaseResult {
        if (item == null) {
            throw new IllegalArgumentException("아이템 정보는 필수입니다");
        }
        if (character == null) {
            throw new IllegalArgumentException("캐릭터 정보는 필수입니다");
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("결제 수단 정보는 필수입니다");
        }
    }
}