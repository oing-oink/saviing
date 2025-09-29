package saviing.game.inventory.application.service;

import saviing.game.inventory.application.dto.query.GetAccessoriesByCharacterQuery;
import saviing.game.inventory.application.dto.query.GetDecorationsByCharacterQuery;
import saviing.game.inventory.application.dto.query.GetInventoriesByCharacterQuery;
import saviing.game.inventory.application.dto.query.GetInventoryQuery;
import saviing.game.inventory.application.dto.query.GetPetsByCharacterQuery;
import saviing.game.inventory.application.dto.query.GetPetsByCharacterAndRoomQuery;
import saviing.game.inventory.application.dto.result.AccessoryInventoryResult;
import saviing.game.inventory.application.dto.result.DecorationInventoryResult;
import saviing.game.inventory.application.dto.result.InventoryListResult;
import saviing.game.inventory.application.dto.result.InventoryResult;
import saviing.game.inventory.application.dto.result.PetInventoryResult;
import saviing.game.inventory.application.mapper.InventoryResultMapper;
import saviing.game.inventory.domain.exception.InventoryItemNotFoundException;
import saviing.game.inventory.domain.model.aggregate.AccessoryInventory;
import saviing.game.inventory.domain.model.aggregate.ConsumptionInventory;
import saviing.game.inventory.domain.model.aggregate.DecorationInventory;
import saviing.game.inventory.domain.model.aggregate.Inventory;
import saviing.game.inventory.domain.model.aggregate.PetInventory;
import saviing.game.inventory.domain.model.enums.ItemCategory;
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
     * 캐릭터의 인벤토리 아이템을 필터링 조건에 따라 조회합니다.
     *
     * @param query 캐릭터별 인벤토리 조회 Query
     * @return 인벤토리 목록 조회 결과
     */
    public InventoryListResult getInventoriesByCharacter(GetInventoriesByCharacterQuery query) {
        List<Inventory> inventories = inventoryRepository.findByCharacterId(query.characterId());

        // 필터링 적용
        List<Inventory> filteredInventories = inventories.stream()
                .filter(inventory -> applyFilters(inventory, query))
                .toList();

        List<InventoryResult> results = filteredInventories.stream()
                .map(inventory -> {
                    ItemResult item = itemQueryService.getItem(
                        GetItemQuery.builder().itemId(inventory.getItemId().value()).build());
                    return resultMapper.toResult(inventory, item);
                })
                .toList();
        return InventoryListResult.of(results);
    }

    /**
     * 인벤토리 아이템에 필터링 조건을 적용합니다.
     *
     * @param inventory 인벤토리 아이템
     * @param query 필터링 조건이 포함된 Query
     * @return 필터링 조건을 만족하는지 여부
     */
    private boolean applyFilters(Inventory inventory, GetInventoriesByCharacterQuery query) {
        // 타입 필터링
        if (query.type() != null && !inventory.getType().equals(query.type())) {
            return false;
        }

        // 사용 여부 필터링
        if (query.isUsed() != null && inventory.isUsed() != query.isUsed()) {
            return false;
        }

        // 카테고리 필터링 (각 타입별로 다르게 처리)
        if (query.category() != null) {
            return matchesCategory(inventory, query.category());
        }

        return true;
    }

    /**
     * 인벤토리 아이템의 카테고리가 필터링 조건과 일치하는지 확인합니다.
     *
     * @param inventory 인벤토리 아이템
     * @param categoryFilter 카테고리 필터링 조건
     * @return 카테고리가 일치하는지 여부
     */
    private boolean matchesCategory(Inventory inventory, ItemCategory categoryFilter) {
        return switch (inventory.getType()) {
            case PET -> {
                if (inventory instanceof PetInventory petInventory) {
                    yield petInventory.getCategory() != null &&
                          petInventory.getCategory().equals(categoryFilter.getDomainCategory());
                }
                yield false;
            }
            case ACCESSORY -> {
                if (inventory instanceof AccessoryInventory accessoryInventory) {
                    yield accessoryInventory.getCategory() != null &&
                          accessoryInventory.getCategory().equals(categoryFilter.getDomainCategory());
                }
                yield false;
            }
            case DECORATION -> {
                if (inventory instanceof DecorationInventory decorationInventory) {
                    yield decorationInventory.getCategory() != null &&
                          decorationInventory.getCategory().equals(categoryFilter.getDomainCategory());
                }
                yield false;
            }
            case CONSUMPTION -> {
                // 소모품도 카테고리 필터링 지원
                if (inventory instanceof ConsumptionInventory consumptionInventory) {
                    yield consumptionInventory.getCategory() != null &&
                          consumptionInventory.getCategory().equals(categoryFilter.getDomainCategory());
                }
                yield false;
            }
        };
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

    /**
     * 캐릭터의 특정 방에 있는 펫 목록을 조회합니다.
     *
     * @param query 캐릭터와 방별 펫 조회 Query
     * @return 해당 방의 펫 인벤토리 목록
     */
    public List<PetInventoryResult> getPetsByCharacterAndRoom(GetPetsByCharacterAndRoomQuery query) {
        List<PetInventory> pets = inventoryRepository.findPetsByCharacterIdAndRoomId(query.characterId(), query.roomId());
        return pets.stream()
                .map(resultMapper::toPetResult)
                .toList();
    }
}