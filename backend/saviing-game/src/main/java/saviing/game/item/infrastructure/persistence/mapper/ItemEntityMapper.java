package saviing.game.item.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import saviing.game.item.domain.model.aggregate.Item;
import saviing.game.item.domain.model.enums.*;
import saviing.game.item.domain.model.enums.category.Category;
import saviing.game.item.domain.model.vo.*;
import saviing.game.item.infrastructure.persistence.entity.ItemEntity;

/**
 * Item 도메인 모델과 ItemEntity 간의 변환을 처리하는 매퍼
 */
@Component
public class ItemEntityMapper {

    /**
     * ItemEntity를 Item 도메인 모델로 변환합니다.
     *
     * @param entity ItemEntity
     * @return Item 도메인 모델
     */
    public Item toDomain(ItemEntity entity) {
        if (entity == null) {
            return null;
        }

        return Item.builder()
            .itemId(entity.getItemId() != null ? ItemId.of(entity.getItemId()) : null)
            .itemName(ItemName.of(entity.getItemName()))
            .itemDescription(ItemDescription.of(entity.getItemDescription()))
            .itemType(parseItemType(entity.getItemType()))
            .itemCategory(parseCategory(entity.getItemCategory()))
            .rarity(parseRarity(entity.getRarity()))
            .itemSize(ItemSize.of(entity.getXLength(), entity.getYLength()))
            .price(Price.of(entity.getCoin(), entity.getFishCoin()))
            .imageUrl(ImageUrl.of(entity.getImageUrl()))
            .isAvailable(entity.getIsAvailable())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    /**
     * Item 도메인 모델을 ItemEntity로 변환합니다.
     *
     * @param item Item 도메인 모델
     * @return ItemEntity
     */
    public ItemEntity toEntity(Item item) {
        if (item == null) {
            return null;
        }

        return ItemEntity.builder()
            .itemId(item.getItemId() != null ? item.getItemId().value() : null)
            .itemName(item.getItemName().value())
            .itemDescription(item.getItemDescription().value())
            .itemType(item.getItemType().name())
            .itemCategory(formatCategory(item.getItemCategory()))
            .rarity(item.getRarity().name())
            .xLength(item.getItemSize().xLength())
            .yLength(item.getItemSize().yLength())
            .coin(item.getPrice().coin())
            .fishCoin(item.getPrice().fishCoin())
            .imageUrl(item.getImageUrl().value())
            .isAvailable(item.isAvailable())
            .createdAt(item.getCreatedAt())
            .updatedAt(item.getUpdatedAt())
            .build();
    }

    /**
     * 문자열을 ItemType enum으로 변환합니다.
     *
     * @param itemType 문자열
     * @return ItemType enum
     */
    private ItemType parseItemType(String itemType) {
        try {
            return ItemType.valueOf(itemType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 아이템 타입: " + itemType, e);
        }
    }

    /**
     * 문자열을 Category로 변환합니다.
     *
     * @param category 문자열 (예: "CAT", "HAT", "FOOD")
     * @return Category
     */
    private Category parseCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("카테고리는 비어있을 수 없습니다");
        }

        try {
            // 각 카테고리 enum에서 순서대로 시도
            try {
                return Pet.valueOf(category);
            } catch (IllegalArgumentException e1) {
                try {
                    return Accessory.valueOf(category);
                } catch (IllegalArgumentException e2) {
                    try {
                        return Decoration.valueOf(category);
                    } catch (IllegalArgumentException e3) {
                        return Consumption.valueOf(category);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 카테고리: " + category, e);
        }
    }

    /**
     * Category를 문자열로 변환합니다.
     *
     * @param category Category
     * @return 문자열 (예: "CAT", "HAT", "FOOD")
     */
    private String formatCategory(Category category) {
        if (category == null) {
            return null;
        }

        // 모든 Category enum은 name() 메서드로 문자열 변환 가능
        return ((Enum<?>) category).name();
    }

    /**
     * 문자열을 Rarity enum으로 변환합니다.
     *
     * @param rarity 문자열
     * @return Rarity enum
     */
    private Rarity parseRarity(String rarity) {
        try {
            return Rarity.valueOf(rarity);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 희귀도: " + rarity, e);
        }
    }
}