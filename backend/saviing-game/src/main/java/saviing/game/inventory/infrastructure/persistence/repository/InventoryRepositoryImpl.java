package saviing.game.inventory.infrastructure.persistence.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.aggregate.AccessoryInventory;
import saviing.game.inventory.domain.model.aggregate.ConsumptionInventory;
import saviing.game.inventory.domain.model.aggregate.DecorationInventory;
import saviing.game.inventory.domain.model.aggregate.Inventory;
import saviing.game.inventory.domain.model.aggregate.PetInventory;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.inventory.domain.repository.InventoryRepository;
import saviing.game.inventory.infrastructure.persistence.entity.AccessoryInventoryEntity;
import saviing.game.inventory.infrastructure.persistence.entity.ConsumptionInventoryEntity;
import saviing.game.inventory.infrastructure.persistence.entity.DecorationInventoryEntity;
import saviing.game.inventory.infrastructure.persistence.entity.InventoryEntity;
import saviing.game.inventory.infrastructure.persistence.entity.PetInventoryEntity;
import saviing.game.inventory.infrastructure.persistence.mapper.InventoryEntityMapper;
import saviing.game.item.domain.model.enums.Accessory;
import saviing.game.item.domain.model.enums.Consumption;
import saviing.game.item.domain.model.enums.Decoration;
import saviing.game.item.domain.model.vo.ItemId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인벤토리 Repository 구현체
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryRepositoryImpl implements InventoryRepository {

    private final InventoryJpaRepository inventoryJpaRepository;
    private final PetInventoryJpaRepository petInventoryJpaRepository;
    private final AccessoryInventoryJpaRepository accessoryInventoryJpaRepository;
    private final DecorationInventoryJpaRepository decorationInventoryJpaRepository;
    private final ConsumptionInventoryJpaRepository consumptionInventoryJpaRepository;
    private final InventoryEntityMapper inventoryEntityMapper;

    @Override
    @Transactional
    public Inventory save(Inventory inventory) {
        InventoryEntity entity = inventoryEntityMapper.toEntity(inventory);
        InventoryEntity savedEntity = inventoryJpaRepository.save(entity);
        return inventoryEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Inventory> findById(InventoryItemId inventoryItemId) {
        return inventoryJpaRepository.findById(inventoryItemId.value())
            .map(inventoryEntityMapper::toDomain);
    }

    @Override
    public List<Inventory> findByCharacterId(CharacterId characterId) {
        return inventoryJpaRepository.findByCharacterId(characterId.value())
            .stream()
            .map(inventoryEntityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findByCharacterIdAndType(CharacterId characterId, InventoryType inventoryType) {
        InventoryEntity.InventoryTypeEntity typeEntity = inventoryEntityMapper.toInventoryTypeEntity(inventoryType);
        return inventoryJpaRepository.findByCharacterIdAndType(characterId.value(), typeEntity)
            .stream()
            .map(inventoryEntityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCharacterIdAndItemId(CharacterId characterId, ItemId itemId) {
        return inventoryJpaRepository.existsByCharacterIdAndItemId(characterId.value(), itemId.value());
    }

    @Override
    public Optional<Long> findItemIdByInventoryItemId(InventoryItemId inventoryItemId) {
        return inventoryJpaRepository.findById(inventoryItemId.value())
            .map(InventoryEntity::getItemId);
    }

    @Override
    @Transactional
    public void deleteById(InventoryItemId inventoryItemId) {
        inventoryJpaRepository.deleteById(inventoryItemId.value());
    }

    // === 펫 특화 메서드 ===


    @Override
    public List<PetInventory> findPetsByCharacterId(CharacterId characterId) {
        return petInventoryJpaRepository.findByCharacterId(characterId.value())
            .stream()
            .map(entity -> (PetInventory) inventoryEntityMapper.toDomain(entity))
            .collect(Collectors.toList());
    }

    @Override
    public List<PetInventory> findUsedPetsByCharacterId(CharacterId characterId) {
        return petInventoryJpaRepository.findByCharacterIdAndIsUsed(characterId.value(), true)
            .stream()
            .map(entity -> (PetInventory) inventoryEntityMapper.toDomain(entity))
            .collect(Collectors.toList());
    }

    @Override
    public List<PetInventory> findPetsByCharacterIdAndRoomId(CharacterId characterId, Long roomId) {
        List<PetInventoryEntity> entities = petInventoryJpaRepository.findByCharacterIdAndRoomId(characterId.value(), roomId);
        List<PetInventory> pets = new ArrayList<>();
        for (PetInventoryEntity entity : entities) {
            pets.add((PetInventory) inventoryEntityMapper.toDomain(entity));
        }
        return pets;
    }

    // === 액세서리 특화 메서드 ===

    @Override
    public Optional<AccessoryInventory> findAccessoryById(InventoryItemId inventoryItemId) {
        return accessoryInventoryJpaRepository.findById(inventoryItemId.value())
            .map(entity -> (AccessoryInventory) inventoryEntityMapper.toDomain(entity));
    }

    @Override
    public List<AccessoryInventory> findAccessoriesByCharacterId(CharacterId characterId) {
        return accessoryInventoryJpaRepository.findByCharacterId(characterId.value())
            .stream()
            .map(entity -> (AccessoryInventory) inventoryEntityMapper.toDomain(entity))
            .collect(Collectors.toList());
    }

    @Override
    public List<AccessoryInventory> findAccessoriesByCharacterIdAndCategory(CharacterId characterId, Accessory category) {
        AccessoryInventoryEntity.AccessoryCategoryEntity categoryEntity = toAccessoryCategoryEntity(category);
        return accessoryInventoryJpaRepository.findByCharacterIdAndCategory(characterId.value(), categoryEntity)
            .stream()
            .map(entity -> (AccessoryInventory) inventoryEntityMapper.toDomain(entity))
            .collect(Collectors.toList());
    }


    // === 데코레이션 특화 메서드 ===


    @Override
    public List<DecorationInventory> findDecorationsByCharacterId(CharacterId characterId) {
        return decorationInventoryJpaRepository.findByCharacterId(characterId.value())
            .stream()
            .map(entity -> (DecorationInventory) inventoryEntityMapper.toDomain(entity))
            .collect(Collectors.toList());
    }

    @Override
    public List<DecorationInventory> findDecorationsByCharacterIdAndCategory(CharacterId characterId, Decoration category) {
        DecorationInventoryEntity.DecorationCategoryEntity categoryEntity = toDecorationCategoryEntity(category);
        return decorationInventoryJpaRepository.findByCharacterIdAndCategory(characterId.value(), categoryEntity)
            .stream()
            .map(entity -> (DecorationInventory) inventoryEntityMapper.toDomain(entity))
            .collect(Collectors.toList());
    }

    @Override
    public List<DecorationInventory> findPlacedDecorationsByCharacterId(CharacterId characterId) {
        return decorationInventoryJpaRepository.findByCharacterIdAndIsUsed(characterId.value(), true)
            .stream()
            .map(entity -> (DecorationInventory) inventoryEntityMapper.toDomain(entity))
            .collect(Collectors.toList());
    }

    // === 소모품 특화 메서드 ===

    @Override
    public Optional<ConsumptionInventory> findConsumptionByCharacterIdAndItemId(CharacterId characterId, ItemId itemId) {
        return consumptionInventoryJpaRepository.findByCharacterIdAndItemId(characterId.value(), itemId.value())
            .map(entity -> (ConsumptionInventory) inventoryEntityMapper.toDomain(entity));
    }



    // === 헬퍼 메서드 ===

    /**
     * Accessory를 AccessoryCategoryEntity로 변환합니다.
     */
    private AccessoryInventoryEntity.AccessoryCategoryEntity toAccessoryCategoryEntity(Accessory accessory) {
        return switch (accessory) {
            case HAT -> AccessoryInventoryEntity.AccessoryCategoryEntity.HAT;
        };
    }

    /**
     * Decoration을 DecorationCategoryEntity로 변환합니다.
     */
    private DecorationInventoryEntity.DecorationCategoryEntity toDecorationCategoryEntity(Decoration decoration) {
        return switch (decoration) {
            case LEFT -> DecorationInventoryEntity.DecorationCategoryEntity.LEFT;
            case RIGHT -> DecorationInventoryEntity.DecorationCategoryEntity.RIGHT;
            case BOTTOM -> DecorationInventoryEntity.DecorationCategoryEntity.BOTTOM;
            case ROOM_COLOR -> DecorationInventoryEntity.DecorationCategoryEntity.ROOM_COLOR;
        };
    }

    /**
     * Consumption을 ConsumptionCategoryEntity로 변환합니다.
     */
    private ConsumptionInventoryEntity.ConsumptionCategoryEntity toConsumptionCategoryEntity(Consumption consumption) {
        return switch (consumption) {
            case TOY -> ConsumptionInventoryEntity.ConsumptionCategoryEntity.TOY;
            case FOOD -> ConsumptionInventoryEntity.ConsumptionCategoryEntity.FOOD;
        };
    }
}