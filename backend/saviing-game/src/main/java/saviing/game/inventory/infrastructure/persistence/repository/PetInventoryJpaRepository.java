package saviing.game.inventory.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

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
}