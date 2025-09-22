package saviing.game.inventory.domain.model.enums;

import saviing.game.item.domain.model.enums.Accessory;
import saviing.game.item.domain.model.enums.Consumption;
import saviing.game.item.domain.model.enums.Decoration;
import saviing.game.item.domain.model.enums.Pet;
import saviing.game.item.domain.model.enums.category.Category;

/**
 * 통합 아이템 카테고리 열거형
 * API 레벨에서 사용하기 위해 모든 아이템 카테고리를 통합한 enum입니다.
 */
public enum ItemCategory {
    // PET 카테고리
    CAT(InventoryType.PET, Pet.CAT),

    // ACCESSORY 카테고리
    HAT(InventoryType.ACCESSORY, Accessory.HAT),

    // DECORATION 카테고리
    LEFT(InventoryType.DECORATION, Decoration.LEFT),
    RIGHT(InventoryType.DECORATION, Decoration.RIGHT),
    BOTTOM(InventoryType.DECORATION, Decoration.BOTTOM),
    ROOM_COLOR(InventoryType.DECORATION, Decoration.ROOM_COLOR),

    // CONSUMPTION 카테고리
    TOY(InventoryType.CONSUMPTION, Consumption.TOY),
    FOOD(InventoryType.CONSUMPTION, Consumption.FOOD);

    private final InventoryType inventoryType;
    private final Category domainCategory;

    ItemCategory(InventoryType inventoryType, Category domainCategory) {
        this.inventoryType = inventoryType;
        this.domainCategory = domainCategory;
    }

    /**
     * 이 카테고리가 속하는 인벤토리 타입을 반환합니다.
     *
     * @return 인벤토리 타입
     */
    public InventoryType getInventoryType() {
        return inventoryType;
    }

    /**
     * 도메인 Category 객체를 반환합니다.
     *
     * @return 도메인 카테고리
     */
    public Category getDomainCategory() {
        return domainCategory;
    }

    /**
     * 이 카테고리가 특정 인벤토리 타입에 속하는지 확인합니다.
     *
     * @param inventoryType 확인할 인벤토리 타입
     * @return 해당 타입에 속하는지 여부
     */
    public boolean belongsTo(InventoryType inventoryType) {
        return this.inventoryType == inventoryType;
    }

    /**
     * 특정 인벤토리 타입에 속하는 모든 카테고리를 반환합니다.
     *
     * @param inventoryType 인벤토리 타입
     * @return 해당 타입에 속하는 카테고리 배열
     */
    public static ItemCategory[] getByInventoryType(InventoryType inventoryType) {
        return switch (inventoryType) {
            case PET -> new ItemCategory[]{CAT};
            case ACCESSORY -> new ItemCategory[]{HAT};
            case DECORATION -> new ItemCategory[]{LEFT, RIGHT, BOTTOM, ROOM_COLOR};
            case CONSUMPTION -> new ItemCategory[]{TOY, FOOD};
        };
    }

    /**
     * 도메인 Category 객체로부터 ItemCategory를 찾습니다.
     *
     * @param category 도메인 카테고리
     * @return 대응되는 ItemCategory
     * @throws IllegalArgumentException 매핑되지 않는 카테고리인 경우
     */
    public static ItemCategory fromDomainCategory(Category category) {
        for (ItemCategory itemCategory : values()) {
            if (itemCategory.domainCategory.equals(category)) {
                return itemCategory;
            }
        }
        throw new IllegalArgumentException("매핑되지 않는 카테고리입니다: " + category);
    }
}