package saviing.game.shop.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import saviing.game.shop.domain.model.vo.PaymentMethod;

import java.time.LocalDateTime;

/**
 * 구매 기록 JPA 엔티티입니다.
 * 성공한 구매만 저장됩니다.
 */
@Entity
@Table(name = "purchase_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long purchaseId;

    @Column(name = "character_id", nullable = false)
    private Long characterId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "paid_amount", nullable = false)
    private Integer paidAmount;

    @Column(name = "paid_currency", nullable = false)
    private String paidCurrency;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @Builder
    public PurchaseRecordEntity(
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
        this.completedAt = completedAt;
    }
}