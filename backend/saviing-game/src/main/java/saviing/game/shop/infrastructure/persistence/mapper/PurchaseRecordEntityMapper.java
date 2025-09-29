package saviing.game.shop.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import saviing.game.shop.domain.model.aggregate.PurchaseRecord;
import saviing.game.shop.infrastructure.persistence.entity.PurchaseRecordEntity;

/**
 * PurchaseRecord와 PurchaseRecordEntity 간 변환 매퍼입니다.
 */
@Component
public class PurchaseRecordEntityMapper {

    /**
     * 도메인 모델을 엔티티로 변환합니다.
     *
     * @param purchaseRecord 도메인 모델
     * @return JPA 엔티티
     */
    public PurchaseRecordEntity toEntity(PurchaseRecord purchaseRecord) {
        return PurchaseRecordEntity.builder()
            .purchaseId(purchaseRecord.getPurchaseId())
            .characterId(purchaseRecord.getCharacterId())
            .itemId(purchaseRecord.getItemId())
            .paymentMethod(purchaseRecord.getPaymentMethod())
            .paidAmount(purchaseRecord.getPaidAmount())
            .paidCurrency(purchaseRecord.getPaidCurrency())
            .completedAt(purchaseRecord.getCompletedAt())
            .build();
    }

    /**
     * 엔티티를 도메인 모델로 변환합니다.
     *
     * @param entity JPA 엔티티
     * @return 도메인 모델
     */
    public PurchaseRecord toDomain(PurchaseRecordEntity entity) {
        return PurchaseRecord.builder()
            .purchaseId(entity.getPurchaseId())
            .characterId(entity.getCharacterId())
            .itemId(entity.getItemId())
            .paymentMethod(entity.getPaymentMethod())
            .paidAmount(entity.getPaidAmount())
            .paidCurrency(entity.getPaidCurrency())
            .completedAt(entity.getCompletedAt())
            .build();
    }
}