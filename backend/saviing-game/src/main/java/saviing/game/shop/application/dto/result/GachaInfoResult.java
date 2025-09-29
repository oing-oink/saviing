package saviing.game.shop.application.dto.result;

import lombok.Builder;
import saviing.game.item.domain.model.enums.Rarity;

import java.util.List;
import java.util.Map;

/**
 * 가챠 정보 조회 결과 DTO.
 */
@Builder
public record GachaInfoResult(
    Long gachaPoolId,
    String gachaPoolName,
    GachaInfo gachaInfo
) {

    @Builder
    public record GachaInfo(
        PriceInfo drawPrice,
        Map<Rarity, Integer> dropRates,
        Map<Rarity, List<ItemInfo>> rewardItemIds
    ) {}

    @Builder
    public record PriceInfo(
        Integer coin,
        Integer fishCoin
    ) {}

    @Builder
    public record ItemInfo(
        Long itemId,
        String itemName
    ) {}
}
