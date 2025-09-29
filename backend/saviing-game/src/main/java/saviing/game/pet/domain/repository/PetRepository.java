package saviing.game.pet.domain.repository;

import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.pet.domain.model.aggregate.Pet;

import java.util.Optional;

/**
 * 펫 도메인의 Repository 인터페이스
 * Pet Aggregate의 영속성을 담당합니다.
 */
public interface PetRepository {

    /**
     * 펫을 저장합니다.
     *
     * @param pet 저장할 펫
     * @return 저장된 펫
     */
    Pet save(Pet pet);

    /**
     * 인벤토리 아이템 ID로 펫을 조회합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID (PK)
     * @return 조회된 펫 (Optional)
     */
    Optional<Pet> findById(InventoryItemId inventoryItemId);

    /**
     * 펫이 존재하는지 확인합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @return 펫 존재 여부
     */
    boolean existsById(InventoryItemId inventoryItemId);

    /**
     * 펫을 삭제합니다.
     *
     * @param inventoryItemId 삭제할 펫의 인벤토리 아이템 ID
     */
    void deleteById(InventoryItemId inventoryItemId);
}