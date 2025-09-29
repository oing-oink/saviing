package saviing.game.shop.domain.model.aggregate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import saviing.game.shop.domain.model.vo.PaymentMethod;

import java.time.LocalDateTime;

/**
 * 구매 완료 기록 Aggregate Root입니다.
 * 성공한 구매만 최종적으로 기록합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseRecord {
    private Long purchaseId;
    private Long characterId;
    private Long itemId;
    private PaymentMethod paymentMethod;
    private Integer paidAmount;
    private String paidCurrency;
    private LocalDateTime completedAt;

    /**
     * PurchaseRecord 생성자입니다.
     */
    @Builder
    public PurchaseRecord(
        Long purchaseId,
        Long characterId,
        Long itemId,
        PaymentMethod paymentMethod,
        Integer paidAmount,
        String paidCurrency,
        LocalDateTime completedAt
    ) {
        this.purchaseId = purchaseId;
        this.characterId = characterId;
        this.itemId = itemId;
        this.paymentMethod = paymentMethod;
        this.paidAmount = paidAmount;
        this.paidCurrency = paidCurrency;
        this.completedAt = completedAt != null ? completedAt : LocalDateTime.now();

        validateInvariants();
    }

    /**
     * 도메인 불변식을 검증합니다.
     */
    private void validateInvariants() {
        if (characterId == null) {
            throw new IllegalArgumentException("캐릭터 ID는 필수입니다");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("아이템 ID는 필수입니다");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("결제 수단은 필수입니다");
        }
        if (paidAmount == null || paidAmount <= 0) {
            throw new IllegalArgumentException("결제 금액은 양수여야 합니다");
        }
    }
}