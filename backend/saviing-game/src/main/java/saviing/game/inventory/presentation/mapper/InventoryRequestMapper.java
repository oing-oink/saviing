package saviing.game.inventory.presentation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import saviing.game.inventory.application.dto.query.GetInventoriesByCharacterQuery;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.item.domain.model.enums.Accessory;
import saviing.game.item.domain.model.enums.Consumption;
import saviing.game.item.domain.model.enums.Decoration;
import saviing.game.item.domain.model.enums.ItemType;
import saviing.game.item.domain.model.enums.Pet;
import saviing.game.item.domain.model.enums.category.Category;

/**
 * Inventory Presentation layer Request를 Application layer Query로 변환하는 Mapper
 * 요청 파라미터를 적절한 Query 객체로 변환하고 검증합니다.
 */
@Component
@RequiredArgsConstructor
public class InventoryRequestMapper {

    /**
     * 요청 파라미터를 GetInventoriesByCharacterQuery로 변환합니다.
     *
     * @param characterId 캐릭터 ID
     * @param type 인벤토리 타입
     * @param category 아이템 카테고리
     * @param isUsed 사용 여부
     * @return GetInventoriesByCharacterQuery
     */
    public GetInventoriesByCharacterQuery toQuery(
        Long characterId, String type, String category, Boolean isUsed
    ) {
        InventoryType inventoryType = parseInventoryType(type);
        String validatedCategory = validateTypeAndCategory(inventoryType, category);

        return GetInventoriesByCharacterQuery.of(
            characterId, inventoryType, validatedCategory, isUsed
        );
    }

    /**
     * 문자열을 InventoryType으로 변환합니다.
     */
    private InventoryType parseInventoryType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        try {
            return InventoryType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 인벤토리 타입: " + type +
                ". 사용 가능한 값: PET, ACCESSORY, DECORATION, CONSUMPTION");
        }
    }

    /**
     * 타입과 카테고리의 조합이 유효한지 검증하고 유효한 카테고리를 반환합니다.
     */
    private String validateTypeAndCategory(InventoryType inventoryType, String category) {
        if (category == null || category.isBlank()) {
            return null;
        }

        // 타입이 지정되지 않은 경우, 모든 카테고리에서 검색
        if (inventoryType == null) {
            Category parsedCategory = parseAnyCategory(category);
            return parsedCategory.name();
        }

        // 타입이 지정된 경우, 해당 타입에 유효한 카테고리인지 검증
        Category parsedCategory = parseCategoryForType(inventoryType, category);
        return parsedCategory.name();
    }

    /**
     * 모든 카테고리 타입에서 유효한 카테고리를 찾습니다.
     */
    private Category parseAnyCategory(String category) {
        // PET 카테고리 시도
        try {
            return Pet.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException ignored) {}

        // ACCESSORY 카테고리 시도
        try {
            return Accessory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException ignored) {}

        // DECORATION 카테고리 시도
        try {
            return Decoration.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException ignored) {}

        // CONSUMPTION 카테고리 시도
        try {
            return Consumption.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException ignored) {}

        throw new IllegalArgumentException("유효하지 않은 카테고리: " + category +
            ". 사용 가능한 값: CAT (펫), HAT (액세서리), LEFT, RIGHT, BOTTOM, ROOM_COLOR (데코레이션), TOY, FOOD (소모품)");
    }

    /**
     * 특정 타입에 대해 유효한 카테고리인지 검증합니다.
     */
    private Category parseCategoryForType(InventoryType inventoryType, String category) {
        ItemType itemType = convertToItemType(inventoryType);

        return switch (itemType) {
            case PET -> {
                try {
                    yield Pet.valueOf(category.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("PET 타입에 유효하지 않은 카테고리: " + category +
                        ". PET 타입에서 사용 가능한 값: CAT");
                }
            }
            case ACCESSORY -> {
                try {
                    yield Accessory.valueOf(category.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("ACCESSORY 타입에 유효하지 않은 카테고리: " + category +
                        ". ACCESSORY 타입에서 사용 가능한 값: HAT");
                }
            }
            case DECORATION -> {
                try {
                    yield Decoration.valueOf(category.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("DECORATION 타입에 유효하지 않은 카테고리: " + category +
                        ". DECORATION 타입에서 사용 가능한 값: LEFT, RIGHT, BOTTOM, ROOM_COLOR");
                }
            }
            case CONSUMPTION -> {
                try {
                    yield Consumption.valueOf(category.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("CONSUMPTION 타입에 유효하지 않은 카테고리: " + category +
                        ". CONSUMPTION 타입에서 사용 가능한 값: TOY, FOOD");
                }
            }
        };
    }

    /**
     * InventoryType을 ItemType으로 변환합니다.
     */
    private ItemType convertToItemType(InventoryType inventoryType) {
        return switch (inventoryType) {
            case PET -> ItemType.PET;
            case ACCESSORY -> ItemType.ACCESSORY;
            case DECORATION -> ItemType.DECORATION;
            case CONSUMPTION -> ItemType.CONSUMPTION;
        };
    }
}