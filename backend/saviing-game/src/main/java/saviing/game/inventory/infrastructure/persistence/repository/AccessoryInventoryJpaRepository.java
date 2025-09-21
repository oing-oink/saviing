package saviing.game.inventory.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import saviing.game.inventory.infrastructure.persistence.entity.AccessoryInventoryEntity;

/**
 * 액세서리 인벤토리 JPA Repository
 */
public interface AccessoryInventoryJpaRepository extends JpaRepository<AccessoryInventoryEntity, Long> {

    /**
     * 캐릭터 ID로 모든 액세서리 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 액세서리 인벤토리 목록
     */
    List<AccessoryInventoryEntity> findByCharacterId(Long characterId);

    /**
     * 캐릭터 ID와 카테고리로 액세서리 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param category 액세서리 카테고리
     * @return 액세서리 인벤토리 목록
     */
    List<AccessoryInventoryEntity> findByCharacterIdAndCategory(
        Long characterId,
        AccessoryInventoryEntity.AccessoryCategoryEntity category
    );

    /**
     * 캐릭터 ID와 사용 여부로 액세서리 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param isUsed 사용 여부
     * @return 액세서리 인벤토리 목록
     */
    List<AccessoryInventoryEntity> findByCharacterIdAndIsUsed(Long characterId, Boolean isUsed);

    /**
     * 캐릭터 ID, 카테고리, 사용 여부로 액세서리 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param category 액세서리 카테고리
     * @param isUsed 사용 여부
     * @return 액세서리 인벤토리 목록
     */
    List<AccessoryInventoryEntity> findByCharacterIdAndCategoryAndIsUsed(
        Long characterId,
        AccessoryInventoryEntity.AccessoryCategoryEntity category,
        Boolean isUsed
    );
}