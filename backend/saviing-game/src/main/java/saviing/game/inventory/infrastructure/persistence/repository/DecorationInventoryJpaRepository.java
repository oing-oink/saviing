package saviing.game.inventory.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import saviing.game.inventory.infrastructure.persistence.entity.DecorationInventoryEntity;

/**
 * 데코레이션 인벤토리 JPA Repository
 */
public interface DecorationInventoryJpaRepository extends JpaRepository<DecorationInventoryEntity, Long> {

    /**
     * 캐릭터 ID로 모든 데코레이션 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 데코레이션 인벤토리 목록
     */
    List<DecorationInventoryEntity> findByCharacterId(Long characterId);

    /**
     * 캐릭터 ID와 카테고리로 데코레이션 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param category 데코레이션 카테고리
     * @return 데코레이션 인벤토리 목록
     */
    List<DecorationInventoryEntity> findByCharacterIdAndCategory(
        Long characterId,
        DecorationInventoryEntity.DecorationCategoryEntity category
    );

    /**
     * 캐릭터 ID와 사용 여부로 데코레이션 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param isUsed 사용 여부
     * @return 데코레이션 인벤토리 목록
     */
    List<DecorationInventoryEntity> findByCharacterIdAndIsUsed(Long characterId, Boolean isUsed);

    /**
     * 캐릭터 ID, 카테고리, 사용 여부로 데코레이션 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param category 데코레이션 카테고리
     * @param isUsed 사용 여부
     * @return 데코레이션 인벤토리 목록
     */
    List<DecorationInventoryEntity> findByCharacterIdAndCategoryAndIsUsed(
        Long characterId,
        DecorationInventoryEntity.DecorationCategoryEntity category,
        Boolean isUsed
    );
}