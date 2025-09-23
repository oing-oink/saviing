package saviing.game.shop.presentation.dto.response;

import lombok.Builder;
import saviing.game.item.domain.model.enums.Rarity;

import java.util.List;
import java.util.Map;

/**
 * 가챠 정보 조회 응답 DTO.
 */
@Builder
public record GachaInfoResponse(
    Long gachaPoolId,
    String gachaPoolName,
    GachaInfo gachaInfo
) {

    @Builder
    public record GachaInfo(
        PriceResponse drawPrice,
        Map<Rarity, Integer> dropRates,
        Map<Rarity, List<ItemResponse>> rewardItemIds
    ) {}

    @Builder
    public record PriceResponse(
        Integer coin,
        Integer fishCoin
    ) {}

    @Builder
    public record ItemResponse(
        Long itemId,
        String itemName
    ) {}
}
