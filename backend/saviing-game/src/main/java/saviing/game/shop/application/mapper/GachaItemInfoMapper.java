package saviing.game.shop.application.mapper;

import org.springframework.stereotype.Component;
import saviing.game.item.domain.model.aggregate.Item;
import saviing.game.shop.application.dto.result.GachaInfoResult;

/**
 * Item 도메인 모델을 GachaInfoResult.ItemInfo로 변환하는 매퍼
 */
@Component
public class GachaItemInfoMapper {

    /**
     * Item 도메인 모델을 GachaInfoResult.ItemInfo로 변환합니다.
     *
     * @param item Item 도메인 모델
     * @return GachaInfoResult.ItemInfo DTO
     */
    public GachaInfoResult.ItemInfo toItemInfo(Item item) {
        if (item == null) {
            return null;
        }

        return GachaInfoResult.ItemInfo.builder()
            .itemId(item.getItemId().value())
            .itemName(item.getItemName().value())
            .build();
    }
}