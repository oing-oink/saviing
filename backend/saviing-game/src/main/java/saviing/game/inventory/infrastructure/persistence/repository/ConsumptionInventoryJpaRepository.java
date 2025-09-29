package saviing.game.inventory.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import saviing.game.inventory.infrastructure.persistence.entity.ConsumptionInventoryEntity;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 소모품 인벤토리 JPA Repository
 */
public interface ConsumptionInventoryJpaRepository extends JpaRepository<ConsumptionInventoryEntity, Long> {

    /**
     * 캐릭터 ID로 소모품 인벤토리를 조회합니다.
     */
    List<ConsumptionInventoryEntity> findByCharacterId(Long characterId);

    /**
     * 캐릭터 ID와 아이템 ID로 소모품 인벤토리를 조회합니다.
     */
    Optional<ConsumptionInventoryEntity> findByCharacterIdAndItemId(Long characterId, Long itemId);

    /**
     * 캐릭터 ID와 카테고리로 소모품 인벤토리를 조회합니다.
     */
    List<ConsumptionInventoryEntity> findByCharacterIdAndCategory(Long characterId, ConsumptionInventoryEntity.ConsumptionCategoryEntity category);
}