package saviing.game.inventory.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // === Room 동기화를 위한 벌크 업데이트 메서드 ===

    /**
     * 특정 방에 배치된 모든 데코레이션 인벤토리의 사용 상태를 false로 업데이트합니다.
     * Room BC에서 방 배치 초기화 시 호출됩니다.
     * roomId 필드를 직접 사용하여 해당 방에 배치된 데코레이션들을 미사용 처리합니다.
     *
     * @param roomId 방 식별자
     * @return 업데이트된 레코드 수
     */
    @Modifying
    @Query("""
        UPDATE DecorationInventoryEntity d
        SET d.isUsed = false, d.roomId = null, d.updatedAt = CURRENT_TIMESTAMP
        WHERE d.roomId = :roomId AND d.isUsed = true
        """)
    int updateUsageToFalseByRoomId(@Param("roomId") Long roomId);

    /**
     * 지정된 인벤토리 아이템들의 사용 상태를 true로 업데이트하고 roomId를 설정합니다.
     * Room BC에서 방 배치 완료 시 호출됩니다.
     *
     * @param inventoryItemIds 사용 중으로 표시할 인벤토리 아이템 ID 목록
     * @param roomId 배치할 방의 식별자
     * @return 업데이트된 레코드 수
     */
    @Modifying
    @Query("""
        UPDATE DecorationInventoryEntity d
        SET d.isUsed = true, d.roomId = :roomId, d.updatedAt = CURRENT_TIMESTAMP
        WHERE d.inventoryItemId IN :inventoryItemIds
        """)
    int updateUsageToTrueByInventoryItemIds(@Param("inventoryItemIds") List<Long> inventoryItemIds, @Param("roomId") Long roomId);
}