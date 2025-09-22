package saviing.game.pet.domain.repository;

import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.pet.domain.model.aggregate.PetInfo;
import saviing.game.pet.domain.model.vo.PetInfoId;

import java.util.Optional;

/**
 * 펫 정보 도메인의 Repository 인터페이스
 * PetInfo Aggregate의 영속성을 담당합니다.
 */
public interface PetInfoRepository {

    /**
     * 펫 정보를 저장합니다.
     *
     * @param petInfo 저장할 펫 정보
     * @return 저장된 펫 정보
     */
    PetInfo save(PetInfo petInfo);

    /**
     * 인벤토리 아이템 ID로 펫 정보를 조회합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID (PK)
     * @return 조회된 펫 정보 (Optional)
     */
    Optional<PetInfo> findById(InventoryItemId inventoryItemId);

    /**
     * 펫 정보가 존재하는지 확인합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @return 펫 정보 존재 여부
     */
    boolean existsById(InventoryItemId inventoryItemId);

    /**
     * 펫 정보를 삭제합니다.
     *
     * @param inventoryItemId 삭제할 펫 정보의 인벤토리 아이템 ID
     */
    void deleteById(InventoryItemId inventoryItemId);
}