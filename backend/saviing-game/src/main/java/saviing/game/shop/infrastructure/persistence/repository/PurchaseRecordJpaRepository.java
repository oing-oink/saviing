package saviing.game.shop.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saviing.game.shop.infrastructure.persistence.entity.PurchaseRecordEntity;

import java.util.List;

/**
 * 구매 기록 JPA 저장소입니다.
 */
@Repository
public interface PurchaseRecordJpaRepository extends JpaRepository<PurchaseRecordEntity, Long> {

    /**
     * 캐릭터 ID로 구매 기록을 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 구매 기록 목록
     */
    List<PurchaseRecordEntity> findByCharacterIdOrderByCompletedAtDesc(Long characterId);
}