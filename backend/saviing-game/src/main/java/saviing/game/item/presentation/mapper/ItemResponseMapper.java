package saviing.game.item.presentation.mapper;

import org.springframework.stereotype.Component;
import saviing.game.item.application.dto.result.ItemListResult;
import saviing.game.item.application.dto.result.ItemResult;
import saviing.game.item.presentation.dto.response.ItemListResponse;
import saviing.game.item.presentation.dto.response.ItemResponse;

/**
 * ItemResult를 ItemResponse로 변환하는 매퍼
 */
@Component
public class ItemResponseMapper {

    /**
     * ItemResult를 ItemResponse로 변환합니다.
     *
     * @param result ItemResult
     * @return ItemResponse
     */
    public ItemResponse toResponse(ItemResult result) {
        if (result == null) {
            return null;
        }

        return ItemResponse.builder()
            .itemId(result.itemId())
            .itemName(result.itemName())
            .itemDescription(result.itemDescription())
            .itemType(result.itemType().name())
            .itemCategory(result.itemCategory().name())
            .rarity(result.rarity().name())
            .xLength(result.xLength())
            .yLength(result.yLength())
            .coin(result.coin())
            .fishCoin(result.fishCoin())
            .imageUrl(result.imageUrl())
            .isAvailable(result.isAvailable())
            .createdAt(result.createdAt())
            .updatedAt(result.updatedAt())
            .build();
    }

    /**
     * ItemListResult를 ItemListResponse로 변환합니다.
     *
     * @param result ItemListResult
     * @return ItemListResponse
     */
    public ItemListResponse toResponse(ItemListResult result) {
        if (result == null) {
            return ItemListResponse.empty();
        }

        return ItemListResponse.builder()
            .items(result.items().stream()
                .map(this::toResponse)
                .toList())
            .totalCount(result.totalCount())
            .build();
    }
}