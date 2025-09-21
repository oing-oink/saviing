package saviing.game.inventory.infrastructure.persistence.mapper;

import java.util.HashMap;
import java.util.Map;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.aggregate.AccessoryInventory;
import saviing.game.inventory.domain.model.aggregate.ConsumptionInventory;
import saviing.game.inventory.domain.model.aggregate.DecorationInventory;
import saviing.game.inventory.domain.model.aggregate.Inventory;
import saviing.game.inventory.domain.model.aggregate.PetInventory;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.inventory.infrastructure.persistence.entity.AccessoryInventoryEntity;
import saviing.game.inventory.infrastructure.persistence.entity.ConsumptionInventoryEntity;
import saviing.game.inventory.infrastructure.persistence.entity.DecorationInventoryEntity;
import saviing.game.inventory.infrastructure.persistence.entity.InventoryEntity;
import saviing.game.inventory.infrastructure.persistence.entity.PetInventoryEntity;
import saviing.game.item.domain.model.enums.Accessory;
import saviing.game.item.domain.model.enums.Consumption;
import saviing.game.item.domain.model.enums.Decoration;
import saviing.game.item.domain.model.enums.Pet;
import saviing.game.item.domain.model.vo.ItemId;

import org.springframework.stereotype.Component;

/**
 * 인벤토리 엔티티와 도메인 모델 간의 매핑을 담당하는 매퍼
 */
@Component
public class InventoryEntityMapper {

    /**
     * 도메인 모델을 JPA 엔티티로 변환합니다.
     *
     * @param inventory 도메인 모델
     * @return JPA 엔티티
     */
    public InventoryEntity toEntity(Inventory inventory) {
        if (inventory instanceof PetInventory petInventory) {
            return toPetEntity(petInventory);
        } else if (inventory instanceof AccessoryInventory accessoryInventory) {
            return toAccessoryEntity(accessoryInventory);
        } else if (inventory instanceof DecorationInventory decorationInventory) {
            return toDecorationEntity(decorationInventory);
        } else if (inventory instanceof ConsumptionInventory consumptionInventory) {
            return toConsumptionEntity(consumptionInventory);
        } else {
            throw new IllegalArgumentException("지원하지 않는 인벤토리 타입입니다: " + inventory.getClass());
        }
    }

    /**
     * JPA 엔티티를 도메인 모델로 변환합니다.
     *
     * @param entity JPA 엔티티
     * @return 도메인 모델
     */
    public Inventory toDomain(InventoryEntity entity) {
        if (entity instanceof PetInventoryEntity petEntity) {
            return toPetDomain(petEntity);
        } else if (entity instanceof AccessoryInventoryEntity accessoryEntity) {
            return toAccessoryDomain(accessoryEntity);
        } else if (entity instanceof DecorationInventoryEntity decorationEntity) {
            return toDecorationDomain(decorationEntity);
        } else if (entity instanceof ConsumptionInventoryEntity consumptionEntity) {
            return toConsumptionDomain(consumptionEntity);
        } else {
            throw new IllegalArgumentException("지원하지 않는 엔티티 타입입니다: " + entity.getClass());
        }
    }

    /**
     * PetInventory를 PetInventoryEntity로 변환합니다.
     */
    private PetInventoryEntity toPetEntity(PetInventory petInventory) {
        return PetInventoryEntity.builder()
            .inventoryItemId(petInventory.getInventoryItemId() != null && petInventory.getInventoryItemId().isAssigned()
                ? petInventory.getInventoryItemId().value() : null)
            .characterId(petInventory.getCharacterId().value())
            .itemId(petInventory.getItemId().value())
            .isUsed(petInventory.isUsed())
            .createdAt(petInventory.getCreatedAt())
            .updatedAt(petInventory.getUpdatedAt())
            .category(toPetCategoryEntity(petInventory.getCategory()))
            .roomId(petInventory.getRoomId())
            .name(null) // Pet 이름은 Pet 도메인에서 관리
            .level(null) // Pet 레벨은 Pet 도메인에서 관리
            .exp(null) // Pet 경험치는 Pet 도메인에서 관리
            .affection(null) // Pet 애정도는 Pet 도메인에서 관리
            .energy(null) // Pet 에너지는 Pet 도메인에서 관리
            .build();
    }

    /**
     * PetInventoryEntity를 PetInventory로 변환합니다.
     */
    private PetInventory toPetDomain(PetInventoryEntity entity) {
        return PetInventory.builder()
            .inventoryItemId(InventoryItemId.of(entity.getInventoryItemId()))
            .characterId(CharacterId.of(entity.getCharacterId()))
            .itemId(ItemId.of(entity.getItemId()))
            .isUsed(entity.getIsUsed())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .category(toPetDomain(entity.getCategory()))
            .roomId(entity.getRoomId())
            .build();
    }

    /**
     * AccessoryInventory를 AccessoryInventoryEntity로 변환합니다.
     */
    private AccessoryInventoryEntity toAccessoryEntity(AccessoryInventory accessoryInventory) {
        return AccessoryInventoryEntity.builder()
            .inventoryItemId(accessoryInventory.getInventoryItemId() != null && accessoryInventory.getInventoryItemId().isAssigned()
                ? accessoryInventory.getInventoryItemId().value() : null)
            .characterId(accessoryInventory.getCharacterId().value())
            .itemId(accessoryInventory.getItemId().value())
            .isUsed(accessoryInventory.isUsed())
            .createdAt(accessoryInventory.getCreatedAt())
            .updatedAt(accessoryInventory.getUpdatedAt())
            .category(toAccessoryCategoryEntity(accessoryInventory.getCategory()))
            .build();
    }

    /**
     * AccessoryInventoryEntity를 AccessoryInventory로 변환합니다.
     */
    private AccessoryInventory toAccessoryDomain(AccessoryInventoryEntity entity) {
        return AccessoryInventory.builder()
            .inventoryItemId(InventoryItemId.of(entity.getInventoryItemId()))
            .characterId(CharacterId.of(entity.getCharacterId()))
            .itemId(ItemId.of(entity.getItemId()))
            .isUsed(entity.getIsUsed())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .category(toAccessoryDomain(entity.getCategory()))
            .build();
    }

    /**
     * DecorationInventory를 DecorationInventoryEntity로 변환합니다.
     */
    private DecorationInventoryEntity toDecorationEntity(DecorationInventory decorationInventory) {
        return DecorationInventoryEntity.builder()
            .inventoryItemId(decorationInventory.getInventoryItemId() != null && decorationInventory.getInventoryItemId().isAssigned()
                ? decorationInventory.getInventoryItemId().value() : null)
            .characterId(decorationInventory.getCharacterId().value())
            .itemId(decorationInventory.getItemId().value())
            .isUsed(decorationInventory.isUsed())
            .createdAt(decorationInventory.getCreatedAt())
            .updatedAt(decorationInventory.getUpdatedAt())
            .category(toDecorationCategoryEntity(decorationInventory.getCategory()))
            .build();
    }

    /**
     * DecorationInventoryEntity를 DecorationInventory로 변환합니다.
     */
    private DecorationInventory toDecorationDomain(DecorationInventoryEntity entity) {
        return DecorationInventory.builder()
            .inventoryItemId(InventoryItemId.of(entity.getInventoryItemId()))
            .characterId(CharacterId.of(entity.getCharacterId()))
            .itemId(ItemId.of(entity.getItemId()))
            .isUsed(entity.getIsUsed())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .category(toDecorationDomain(entity.getCategory()))
            .build();
    }

    /**
     * Accessory를 AccessoryCategoryEntity로 변환합니다.
     */
    private AccessoryInventoryEntity.AccessoryCategoryEntity toAccessoryCategoryEntity(Accessory accessory) {
        return switch (accessory) {
            case HAT -> AccessoryInventoryEntity.AccessoryCategoryEntity.HAT;
        };
    }

    /**
     * AccessoryCategoryEntity를 Accessory로 변환합니다.
     */
    private Accessory toAccessoryDomain(AccessoryInventoryEntity.AccessoryCategoryEntity entity) {
        return switch (entity) {
            case HAT -> Accessory.HAT;
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
     * DecorationCategoryEntity를 Decoration으로 변환합니다.
     */
    private Decoration toDecorationDomain(DecorationInventoryEntity.DecorationCategoryEntity entity) {
        return switch (entity) {
            case LEFT -> Decoration.LEFT;
            case RIGHT -> Decoration.RIGHT;
            case BOTTOM -> Decoration.BOTTOM;
            case ROOM_COLOR -> Decoration.ROOM_COLOR;
        };
    }

    /**
     * Pet을 PetCategoryEntity로 변환합니다.
     */
    private PetInventoryEntity.PetCategoryEntity toPetCategoryEntity(Pet pet) {
        return switch (pet) {
            case CAT -> PetInventoryEntity.PetCategoryEntity.CAT;
        };
    }

    /**
     * PetCategoryEntity를 Pet으로 변환합니다.
     */
    private Pet toPetDomain(PetInventoryEntity.PetCategoryEntity entity) {
        return switch (entity) {
            case CAT -> Pet.CAT;
        };
    }

    /**
     * InventoryType을 InventoryTypeEntity로 변환합니다.
     */
    public InventoryEntity.InventoryTypeEntity toInventoryTypeEntity(InventoryType inventoryType) {
        return switch (inventoryType) {
            case PET -> InventoryEntity.InventoryTypeEntity.PET;
            case ACCESSORY -> InventoryEntity.InventoryTypeEntity.ACCESSORY;
            case DECORATION -> InventoryEntity.InventoryTypeEntity.DECORATION;
            case CONSUMPTION -> InventoryEntity.InventoryTypeEntity.CONSUMPTION;
        };
    }

    /**
     * InventoryTypeEntity를 InventoryType으로 변환합니다.
     */
    public InventoryType toInventoryTypeDomain(InventoryEntity.InventoryTypeEntity entity) {
        return switch (entity) {
            case PET -> InventoryType.PET;
            case ACCESSORY -> InventoryType.ACCESSORY;
            case DECORATION -> InventoryType.DECORATION;
            case CONSUMPTION -> InventoryType.CONSUMPTION;
        };
    }

    /**
     * ConsumptionInventory를 ConsumptionInventoryEntity로 변환합니다.
     */
    private ConsumptionInventoryEntity toConsumptionEntity(ConsumptionInventory consumptionInventory) {
        return ConsumptionInventoryEntity.builder()
            .inventoryItemId(consumptionInventory.getInventoryItemId() != null && consumptionInventory.getInventoryItemId().isAssigned()
                ? consumptionInventory.getInventoryItemId().value() : null)
            .characterId(consumptionInventory.getCharacterId().value())
            .itemId(consumptionInventory.getItemId().value())
            .isUsed(consumptionInventory.isUsed())
            .createdAt(consumptionInventory.getCreatedAt())
            .updatedAt(consumptionInventory.getUpdatedAt())
            .category(toConsumptionCategoryEntity(consumptionInventory.getCategory()))
            .count(consumptionInventory.getCount())
            .build();
    }

    /**
     * ConsumptionInventoryEntity를 ConsumptionInventory로 변환합니다.
     */
    private ConsumptionInventory toConsumptionDomain(ConsumptionInventoryEntity entity) {
        return ConsumptionInventory.builder()
            .inventoryItemId(InventoryItemId.of(entity.getInventoryItemId()))
            .characterId(CharacterId.of(entity.getCharacterId()))
            .itemId(ItemId.of(entity.getItemId()))
            .isUsed(entity.getIsUsed())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .category(toConsumptionDomain(entity.getCategory()))
            .count(entity.getCount())
            .build();
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

    /**
     * ConsumptionCategoryEntity를 Consumption으로 변환합니다.
     */
    private Consumption toConsumptionDomain(ConsumptionInventoryEntity.ConsumptionCategoryEntity entity) {
        return switch (entity) {
            case TOY -> Consumption.TOY;
            case FOOD -> Consumption.FOOD;
        };
    }
}