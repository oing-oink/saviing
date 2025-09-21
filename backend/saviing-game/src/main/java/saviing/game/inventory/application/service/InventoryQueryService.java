package saviing.game.inventory.application.service;

import saviing.game.inventory.application.dto.query.GetAccessoriesByCharacterQuery;
import saviing.game.inventory.application.dto.query.GetDecorationsByCharacterQuery;
import saviing.game.inventory.application.dto.query.GetInventoriesByCharacterQuery;
import saviing.game.inventory.application.dto.query.GetInventoryQuery;
import saviing.game.inventory.application.dto.query.GetPetsByCharacterQuery;
import saviing.game.inventory.application.dto.result.AccessoryInventoryResult;
import saviing.game.inventory.application.dto.result.DecorationInventoryResult;
import saviing.game.inventory.application.dto.result.InventoryListResult;
import saviing.game.inventory.application.dto.result.InventoryResult;
import saviing.game.inventory.application.dto.result.PetInventoryResult;
import saviing.game.inventory.application.mapper.InventoryResultMapper;
import saviing.game.inventory.domain.exception.InventoryItemNotFoundException;
import saviing.game.inventory.domain.model.aggregate.AccessoryInventory;
import saviing.game.inventory.domain.model.aggregate.DecorationInventory;
import saviing.game.inventory.domain.model.aggregate.Inventory;
import saviing.game.inventory.domain.model.aggregate.PetInventory;
import saviing.game.inventory.domain.repository.InventoryRepository;
import saviing.game.item.application.dto.query.GetItemQuery;
import saviing.game.item.application.dto.result.ItemResult;
import saviing.game.item.application.service.ItemQueryService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인벤토리 Query 처리 서비스
 * 조회를 담당하는 Query 처리를 담당합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InventoryQueryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryResultMapper resultMapper;
    private final ItemQueryService itemQueryService;

    /**
     * 인벤토리 아이템 상세 정보를 조회합니다.
     *
     * @param query 인벤토리 조회 Query
     * @return 인벤토리 조회 결과
     */
    public InventoryResult getInventory(GetInventoryQuery query) {
        Inventory inventory = inventoryRepository.findById(query.inventoryItemId())
                .orElseThrow(() -> new InventoryItemNotFoundException(query.inventoryItemId()));

        ItemResult item = itemQueryService.getItem(
            GetItemQuery.builder().itemId(inventory.getItemId().value()).build());

        return resultMapper.toResult(inventory, item);
    }

    /**
     * 캐릭터의 모든 인벤토리 아이템을 조회합니다.
     *
     * @param query 캐릭터별 인벤토리 조회 Query
     * @return 인벤토리 목록 조회 결과
     */
    public InventoryListResult getInventoriesByCharacter(GetInventoriesByCharacterQuery query) {
        List<Inventory> inventories = inventoryRepository.findByCharacterId(query.characterId());
        List<InventoryResult> results = inventories.stream()
                .map(inventory -> {
                    ItemResult item = itemQueryService.getItem(
                        GetItemQuery.builder().itemId(inventory.getItemId().value()).build());
                    return resultMapper.toResult(inventory, item);
                })
                .toList();
        return InventoryListResult.of(results);
    }

    /**
     * 캐릭터의 펫 목록을 조회합니다.
     *
     * @param query 캐릭터별 펫 조회 Query
     * @return 펫 인벤토리 목록
     */
    public List<PetInventoryResult> getPetsByCharacter(GetPetsByCharacterQuery query) {
        List<PetInventory> pets = inventoryRepository.findPetsByCharacterId(query.characterId());
        return pets.stream()
                .map(resultMapper::toPetResult)
                .toList();
    }

    /**
     * 캐릭터가 사용 중인 펫 목록을 조회합니다.
     *
     * @param query 캐릭터별 펫 조회 Query
     * @return 사용 중인 펫 인벤토리 목록
     */
    public List<PetInventoryResult> getUsedPetsByCharacter(GetPetsByCharacterQuery query) {
        List<PetInventory> pets = inventoryRepository.findUsedPetsByCharacterId(query.characterId());
        return pets.stream()
                .map(resultMapper::toPetResult)
                .toList();
    }

    /**
     * 캐릭터의 액세서리 목록을 조회합니다.
     *
     * @param query 캐릭터별 액세서리 조회 Query
     * @return 액세서리 인벤토리 목록
     */
    public List<AccessoryInventoryResult> getAccessoriesByCharacter(GetAccessoriesByCharacterQuery query) {
        List<AccessoryInventory> accessories;

        if (query.category() != null) {
            accessories = inventoryRepository.findAccessoriesByCharacterIdAndCategory(
                    query.characterId(), query.category());
        } else {
            accessories = inventoryRepository.findAccessoriesByCharacterId(query.characterId());
        }

        return accessories.stream()
                .map(resultMapper::toAccessoryResult)
                .toList();
    }


    /**
     * 캐릭터의 데코레이션 목록을 조회합니다.
     *
     * @param query 캐릭터별 데코레이션 조회 Query
     * @return 데코레이션 인벤토리 목록
     */
    public List<DecorationInventoryResult> getDecorationsByCharacter(GetDecorationsByCharacterQuery query) {
        List<DecorationInventory> decorations;

        if (query.category() != null) {
            decorations = inventoryRepository.findDecorationsByCharacterIdAndCategory(
                    query.characterId(), query.category());
        } else {
            decorations = inventoryRepository.findDecorationsByCharacterId(query.characterId());
        }

        return decorations.stream()
                .map(resultMapper::toDecorationResult)
                .toList();
    }

    /**
     * 캐릭터가 배치한 데코레이션 목록을 조회합니다.
     *
     * @param query 캐릭터별 데코레이션 조회 Query
     * @return 배치된 데코레이션 인벤토리 목록
     */
    public List<DecorationInventoryResult> getPlacedDecorationsByCharacter(GetDecorationsByCharacterQuery query) {
        List<DecorationInventory> decorations = inventoryRepository.findPlacedDecorationsByCharacterId(query.characterId());
        return decorations.stream()
                .map(resultMapper::toDecorationResult)
                .toList();
    }
}