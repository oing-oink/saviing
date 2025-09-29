package saviing.game.shop.domain.repository;

import saviing.game.shop.domain.model.aggregate.PurchaseRecord;

import java.util.List;
import java.util.Optional;

/**
 * 구매 기록 저장소 인터페이스입니다.
 * 성공한 구매 기록만 관리합니다.
 */
public interface PurchaseRecordRepository {

    /**
     * 구매 기록을 저장합니다.
     *
     * @param purchaseRecord 구매 기록
     * @return 저장된 구매 기록
     */
    PurchaseRecord save(PurchaseRecord purchaseRecord);

    /**
     * 구매 ID로 구매 기록을 조회합니다.
     *
     * @param purchaseId 구매 ID
     * @return 구매 기록
     */
    Optional<PurchaseRecord> findById(Long purchaseId);

    /**
     * 캐릭터의 구매 기록 목록을 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 구매 기록 목록
     */
    List<PurchaseRecord> findByCharacterId(Long characterId);
}