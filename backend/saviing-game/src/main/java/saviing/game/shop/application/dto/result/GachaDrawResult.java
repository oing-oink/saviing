package saviing.game.shop.application.dto.result;

import lombok.Builder;
import saviing.game.character.application.dto.result.CharacterResult;
import saviing.game.item.application.dto.result.ItemResult;

/**
 * 가챠 뽑기 결과 DTO
 *
 * @param gachaPoolId 가챠풀 ID
 * @param gachaPoolName 가챠풀 이름
 * @param drawnItem 뽑은 아이템 정보
 * @param character 캐릭터 정보 (잔액 포함)
 * @param paymentCurrency 결제에 사용된 화폐 종류
 */
@Builder
public record GachaDrawResult(
    Long gachaPoolId,
    String gachaPoolName,
    ItemResult drawnItem,
    CharacterResult character,
    String paymentCurrency
) {

    /**
     * 결과의 유효성을 검증합니다.
     *
     * @throws IllegalArgumentException 유효하지 않은 값이 있는 경우
     */
    public void validate() {
        if (gachaPoolId == null || gachaPoolId <= 0) {
            throw new IllegalArgumentException("가챠풀 ID는 양수여야 합니다");
        }
        if (gachaPoolName == null || gachaPoolName.trim().isEmpty()) {
            throw new IllegalArgumentException("가챠풀 이름은 필수입니다");
        }
        if (drawnItem == null) {
            throw new IllegalArgumentException("뽑은 아이템 정보는 필수입니다");
        }
        if (character == null) {
            throw new IllegalArgumentException("캐릭터 정보는 필수입니다");
        }
        if (paymentCurrency == null || paymentCurrency.trim().isEmpty()) {
            throw new IllegalArgumentException("결제 화폐 정보는 필수입니다");
        }
    }

    /**
     * 뽑은 아이템의 희귀도를 반환합니다.
     *
     * @return 아이템 희귀도
     */
    public String getDrawnItemRarity() {
        return drawnItem != null ? drawnItem.rarity().name() : null;
    }

    /**
     * 뽑은 아이템의 이름을 반환합니다.
     *
     * @return 아이템 이름
     */
    public String getDrawnItemName() {
        return drawnItem != null ? drawnItem.itemName() : null;
    }
}
