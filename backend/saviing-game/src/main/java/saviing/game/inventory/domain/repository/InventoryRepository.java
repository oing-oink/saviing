package saviing.game.inventory.domain.repository;

import java.util.List;
import java.util.Optional;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.aggregate.Inventory;
import saviing.game.inventory.domain.model.aggregate.AccessoryInventory;
import saviing.game.inventory.domain.model.aggregate.ConsumptionInventory;
import saviing.game.inventory.domain.model.aggregate.DecorationInventory;
import saviing.game.inventory.domain.model.aggregate.PetInventory;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.item.domain.model.enums.Accessory;
import saviing.game.item.domain.model.enums.Consumption;
import saviing.game.item.domain.model.enums.Decoration;
import saviing.game.item.domain.model.vo.ItemId;

/**
 * 인벤토리 Repository 인터페이스
 * 인벤토리 관련 데이터 접근을 담당합니다.
 */
public interface InventoryRepository {

    /**
     * 인벤토리 아이템을 저장합니다.
     *
     * @param inventory 저장할 인벤토리 아이템
     * @return 저장된 인벤토리 아이템
     */
    Inventory save(Inventory inventory);

    /**
     * 인벤토리 아이템 ID로 조회합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @return 인벤토리 아이템 (Optional)
     */
    Optional<Inventory> findById(InventoryItemId inventoryItemId);

    /**
     * 캐릭터의 모든 인벤토리 아이템을 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 인벤토리 아이템 목록
     */
    List<Inventory> findByCharacterId(CharacterId characterId);

    /**
     * 캐릭터의 특정 타입 인벤토리 아이템을 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param inventoryType 인벤토리 타입
     * @return 인벤토리 아이템 목록
     */
    List<Inventory> findByCharacterIdAndType(CharacterId characterId, InventoryType inventoryType);

    /**
     * 캐릭터가 특정 아이템을 가지고 있는지 확인합니다.
     *
     * @param characterId 캐릭터 ID
     * @param itemId 아이템 ID
     * @return 소유 여부
     */
    boolean existsByCharacterIdAndItemId(CharacterId characterId, ItemId itemId);

    /**
     * 인벤토리 아이템을 삭제합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     */
    void deleteById(InventoryItemId inventoryItemId);

    // === 펫 특화 메서드 ===

    /**
     * 펫 인벤토리 아이템 ID로 펫을 조회합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @return 펫 인벤토리 (Optional)
     */
    Optional<PetInventory> findPetById(InventoryItemId inventoryItemId);

    /**
     * 캐릭터의 모든 펫 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 펫 인벤토리 목록
     */
    List<PetInventory> findPetsByCharacterId(CharacterId characterId);

    /**
     * 캐릭터의 사용 중인 펫 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 사용 중인 펫 인벤토리 목록
     */
    List<PetInventory> findUsedPetsByCharacterId(CharacterId characterId);

    // === 액세서리 특화 메서드 ===

    /**
     * 액세서리 인벤토리 아이템 ID로 액세서리를 조회합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @return 액세서리 인벤토리 (Optional)
     */
    Optional<AccessoryInventory> findAccessoryById(InventoryItemId inventoryItemId);

    /**
     * 캐릭터의 모든 액세서리 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 액세서리 인벤토리 목록
     */
    List<AccessoryInventory> findAccessoriesByCharacterId(CharacterId characterId);

    /**
     * 캐릭터의 특정 카테고리 액세서리 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param category 액세서리 카테고리
     * @return 액세서리 인벤토리 목록
     */
    List<AccessoryInventory> findAccessoriesByCharacterIdAndCategory(CharacterId characterId, Accessory category);


    // === 데코레이션 특화 메서드 ===

    /**
     * 데코레이션 인벤토리 아이템 ID로 데코레이션을 조회합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @return 데코레이션 인벤토리 (Optional)
     */
    Optional<DecorationInventory> findDecorationById(InventoryItemId inventoryItemId);

    /**
     * 캐릭터의 모든 데코레이션 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 데코레이션 인벤토리 목록
     */
    List<DecorationInventory> findDecorationsByCharacterId(CharacterId characterId);

    /**
     * 캐릭터의 특정 카테고리 데코레이션 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param category 데코레이션 카테고리
     * @return 데코레이션 인벤토리 목록
     */
    List<DecorationInventory> findDecorationsByCharacterIdAndCategory(CharacterId characterId, Decoration category);

    /**
     * 캐릭터의 배치된 데코레이션 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 배치된 데코레이션 인벤토리 목록
     */
    List<DecorationInventory> findPlacedDecorationsByCharacterId(CharacterId characterId);

    // === 소모품 특화 메서드 ===

    /**
     * 캐릭터와 아이템 ID로 소모품 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param itemId 아이템 ID
     * @return 소모품 인벤토리 (Optional)
     */
    Optional<ConsumptionInventory> findConsumptionByCharacterIdAndItemId(CharacterId characterId, ItemId itemId);

    /**
     * 캐릭터의 모든 소모품 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 소모품 인벤토리 목록
     */
    List<ConsumptionInventory> findConsumptionsByCharacterId(CharacterId characterId);

    /**
     * 캐릭터의 특정 카테고리 소모품 인벤토리를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param category 소모품 카테고리
     * @return 소모품 인벤토리 목록
     */
    List<ConsumptionInventory> findConsumptionsByCharacterIdAndCategory(CharacterId characterId, Consumption category);
}