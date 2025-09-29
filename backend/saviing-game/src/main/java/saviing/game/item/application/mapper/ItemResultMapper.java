package saviing.game.item.application.mapper;

import org.springframework.stereotype.Component;
import saviing.game.item.application.dto.result.ItemResult;
import saviing.game.item.domain.model.aggregate.Item;

/**
 * Item 도메인 모델을 ItemResult DTO로 변환하는 매퍼
 */
@Component
public class ItemResultMapper {

    /**
     * Item 도메인 모델을 ItemResult로 변환합니다.
     *
     * @param item Item 도메인 모델
     * @return ItemResult DTO
     */
    public ItemResult toResult(Item item) {
        if (item == null) {
            return null;
        }

        return ItemResult.builder()
            .itemId(item.getItemId() != null ? item.getItemId().value() : null)
            .itemName(item.getItemName().value())
            .itemDescription(item.getItemDescription().value())
            .itemType(item.getItemType())
            .itemCategory(item.getItemCategory())
            .rarity(item.getRarity())
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
}