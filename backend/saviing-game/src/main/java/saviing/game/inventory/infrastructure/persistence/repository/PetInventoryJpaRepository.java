package saviing.game.inventory.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import saviing.game.inventory.infrastructure.persistence.entity.PetInventoryEntity;

/**
 * 펫 인벤토리 JPA Repository
 */
public interface PetInventoryJpaRepository extends JpaRepository<PetInventoryEntity, Long> {

    /**
     * 캐릭터 ID로 모든 펫 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 펫 인벤토리 목록
     */
    List<PetInventoryEntity> findByCharacterId(Long characterId);

    /**
     * 캐릭터 ID와 사용 여부로 펫 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param isUsed 사용 여부
     * @return 펫 인벤토리 목록
     */
    List<PetInventoryEntity> findByCharacterIdAndIsUsed(Long characterId, Boolean isUsed);

    /**
     * 방 ID로 펫 인벤토리를 조회합니다.
     *
     * @param roomId 방 ID
     * @return 펫 인벤토리 목록
     */
    List<PetInventoryEntity> findByRoomId(Long roomId);

    /**
     * 캐릭터 ID와 방 ID로 펫 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param roomId 방 ID
     * @return 펫 인벤토리 목록
     */
    List<PetInventoryEntity> findByCharacterIdAndRoomId(Long characterId, Long roomId);

    /**
     * 인벤토리 아이템 ID 목록에 해당하는 펫들을 특정 방에 배치합니다.
     *
     * @param inventoryItemIds 인벤토리 아이템 ID 목록
     * @param roomId 배치할 방 ID
     * @return 업데이트된 행의 개수
     */
    @Modifying
    @Query("UPDATE PetInventoryEntity p SET p.roomId = :roomId WHERE p.inventoryItemId IN :inventoryItemIds")
    int updateRoomIdByInventoryItemIds(@Param("inventoryItemIds") List<Long> inventoryItemIds, @Param("roomId") Long roomId);

    /**
     * 특정 방에 배치된 모든 펫들의 방 ID를 null로 초기화합니다.
     *
     * @param roomId 초기화할 방 ID
     * @return 업데이트된 행의 개수
     */
    @Modifying
    @Query("UPDATE PetInventoryEntity p SET p.roomId = null WHERE p.roomId = :roomId")
    int resetRoomIdByRoomId(@Param("roomId") Long roomId);
}