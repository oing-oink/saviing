package saviing.game.inventory.application.mapper;

import saviing.game.inventory.application.dto.result.AccessoryInventoryResult;
import saviing.game.inventory.application.dto.result.DecorationInventoryResult;
import saviing.game.inventory.application.dto.result.InventoryResult;
import saviing.game.inventory.application.dto.result.PetInventoryResult;
import saviing.game.inventory.domain.model.aggregate.AccessoryInventory;
import saviing.game.inventory.domain.model.aggregate.ConsumptionInventory;
import saviing.game.inventory.domain.model.aggregate.DecorationInventory;
import saviing.game.inventory.domain.model.aggregate.Inventory;
import saviing.game.inventory.domain.model.aggregate.PetInventory;
import saviing.game.item.application.dto.result.ItemResult;

import org.springframework.stereotype.Component;

/**
 * Inventory 도메인 객체를 Result로 변환하는 Mapper
 * Application 계층에서 Domain 객체를 Result DTO로 변환합니다.
 */
@Component
public class InventoryResultMapper {

    /**
     * Inventory 도메인 객체를 InventoryResult로 변환합니다.
     *
     * @param inventory Inventory 도메인 객체
     * @param item Item 정보
     * @return InventoryResult
     */
    public InventoryResult toResult(Inventory inventory, ItemResult item) {
        if (inventory == null) {
            return null;
        }

        Long roomId = getRoomIdForInventory(inventory);
        Long petInventoryItemId = getPetInventoryItemIdForInventory(inventory);
        Integer count = getCountForInventory(inventory);

        return InventoryResult.builder()
            .inventoryItemId(inventory.getInventoryItemId() != null ? inventory.getInventoryItemId().value() : null)
            .characterId(inventory.getCharacterId().value())
            .itemId(inventory.getItemId().value())
            .type(inventory.getType())
            .isUsed(inventory.isUsed())
            .itemName(item.itemName())
            .itemDescription(item.itemDescription())
            .itemCategory(item.itemCategory().name())
            .imageUrl(item.imageUrl())
            .rarity(item.rarity())
            .xLength(item.xLength())
            .yLength(item.yLength())
            .roomId(roomId)
            .petInventoryItemId(petInventoryItemId)
            .count(count)
            .createdAt(inventory.getCreatedAt())
            .updatedAt(inventory.getUpdatedAt())
            .build();
    }

    /**
     * 인벤토리 타입에 따른 roomId를 반환합니다.
     */
    private Long getRoomIdForInventory(Inventory inventory) {
        return switch (inventory.getType()) {
            case PET -> {
                if (inventory instanceof PetInventory petInventory && inventory.isUsed()) {
                    yield petInventory.getRoomId();
                }
                yield null;
            }
            case DECORATION -> {
                // TODO: 데코레이션 배치 이벤트 처리 후 Room 도메인에서 roomId 조회
                yield null;
            }
            case ACCESSORY -> null; // 액세서리는 roomId가 없음
            case CONSUMPTION -> null; // 소모품은 roomId가 없음
        };
    }

    /**
     * 인벤토리 타입에 따른 petInventoryItemId를 반환합니다.
     */
    private Long getPetInventoryItemIdForInventory(Inventory inventory) {
        return switch (inventory.getType()) {
            case ACCESSORY -> {
                if (inventory instanceof AccessoryInventory accessoryInventory && inventory.isUsed()) {
                    yield accessoryInventory.getPetInventoryItemId() != null ?
                        accessoryInventory.getPetInventoryItemId().value() : null;
                }
                yield null;
            }
            case PET, DECORATION, CONSUMPTION -> null;
        };
    }

    /**
     * 인벤토리 타입에 따른 count를 반환합니다.
     */
    private Integer getCountForInventory(Inventory inventory) {
        return switch (inventory.getType()) {
            case CONSUMPTION -> {
                if (inventory instanceof ConsumptionInventory consumptionInventory) {
                    yield consumptionInventory.getCount();
                }
                yield null;
            }
            case PET, ACCESSORY, DECORATION -> null;
        };
    }


    /**
     * PetInventory 도메인 객체를 PetInventoryResult로 변환합니다.
     *
     * @param petInventory PetInventory 도메인 객체
     * @return PetInventoryResult
     */
    public PetInventoryResult toPetResult(PetInventory petInventory) {
        if (petInventory == null) {
            return null;
        }

        return PetInventoryResult.builder()
            .inventoryItemId(petInventory.getInventoryItemId() != null ? petInventory.getInventoryItemId().value() : null)
            .characterId(petInventory.getCharacterId().value())
            .itemId(petInventory.getItemId().value())
            .type(petInventory.getType())
            .isUsed(petInventory.isUsed())
            .roomId(petInventory.getRoomId())
            .createdAt(petInventory.getCreatedAt())
            .updatedAt(petInventory.getUpdatedAt())
            .build();
    }

    /**
     * AccessoryInventory 도메인 객체를 AccessoryInventoryResult로 변환합니다.
     *
     * @param accessoryInventory AccessoryInventory 도메인 객체
     * @return AccessoryInventoryResult
     */
    public AccessoryInventoryResult toAccessoryResult(AccessoryInventory accessoryInventory) {
        if (accessoryInventory == null) {
            return null;
        }

        return AccessoryInventoryResult.builder()
            .inventoryItemId(accessoryInventory.getInventoryItemId() != null ?
                accessoryInventory.getInventoryItemId().value() : null)
            .characterId(accessoryInventory.getCharacterId().value())
            .itemId(accessoryInventory.getItemId().value())
            .type(accessoryInventory.getType())
            .isUsed(accessoryInventory.isUsed())
            .category(accessoryInventory.getCategory() != null ? accessoryInventory.getCategory().name() : null)
            .petInventoryItemId(accessoryInventory.getPetInventoryItemId() != null ?
                accessoryInventory.getPetInventoryItemId().value() : null)
            .createdAt(accessoryInventory.getCreatedAt())
            .updatedAt(accessoryInventory.getUpdatedAt())
            .build();
    }

    /**
     * DecorationInventory 도메인 객체를 DecorationInventoryResult로 변환합니다.
     *
     * @param decorationInventory DecorationInventory 도메인 객체
     * @return DecorationInventoryResult
     */
    public DecorationInventoryResult toDecorationResult(DecorationInventory decorationInventory) {
        if (decorationInventory == null) {
            return null;
        }

        return DecorationInventoryResult.builder()
            .inventoryItemId(decorationInventory.getInventoryItemId() != null ?
                decorationInventory.getInventoryItemId().value() : null)
            .characterId(decorationInventory.getCharacterId().value())
            .itemId(decorationInventory.getItemId().value())
            .type(decorationInventory.getType())
            .isUsed(decorationInventory.isUsed())
            .category(decorationInventory.getCategory() != null ? decorationInventory.getCategory().name() : null)
            .roomId(null) // TODO: Implement decoration placement tracking
            .xPosition(null) // TODO: Implement decoration position tracking
            .yPosition(null) // TODO: Implement decoration position tracking
            .createdAt(decorationInventory.getCreatedAt())
            .updatedAt(decorationInventory.getUpdatedAt())
            .build();
    }
}