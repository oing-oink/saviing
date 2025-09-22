package saviing.game.inventory.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import saviing.game.inventory.infrastructure.persistence.entity.InventoryEntity;

/**
 * 인벤토리 JPA Repository
 */
public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, Long> {

    /**
     * 캐릭터 ID로 모든 인벤토리 아이템을 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 인벤토리 아이템 목록
     */
    List<InventoryEntity> findByCharacterId(Long characterId);

    /**
     * 캐릭터 ID와 인벤토리 타입으로 인벤토리 아이템을 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param type 인벤토리 타입
     * @return 인벤토리 아이템 목록
     */
    List<InventoryEntity> findByCharacterIdAndType(Long characterId, InventoryEntity.InventoryTypeEntity type);

    /**
     * 캐릭터 ID와 아이템 ID로 인벤토리 아이템 존재 여부를 확인합니다.
     *
     * @param characterId 캐릭터 ID
     * @param itemId 아이템 ID
     * @return 존재 여부
     */
    boolean existsByCharacterIdAndItemId(Long characterId, Long itemId);

    /**
     * 캐릭터 ID와 사용 여부로 인벤토리 아이템을 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param isUsed 사용 여부
     * @return 인벤토리 아이템 목록
     */
    List<InventoryEntity> findByCharacterIdAndIsUsed(Long characterId, Boolean isUsed);

    /**
     * 캐릭터 ID, 인벤토리 타입, 사용 여부로 인벤토리 아이템을 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param type 인벤토리 타입
     * @param isUsed 사용 여부
     * @return 인벤토리 아이템 목록
     */
    List<InventoryEntity> findByCharacterIdAndTypeAndIsUsed(
        Long characterId,
        InventoryEntity.InventoryTypeEntity type,
        Boolean isUsed
    );
}