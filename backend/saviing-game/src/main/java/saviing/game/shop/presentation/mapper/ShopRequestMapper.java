package saviing.game.shop.presentation.mapper;

import org.springframework.stereotype.Component;
import saviing.game.shop.application.dto.command.PurchaseItemCommand;
import saviing.game.shop.presentation.dto.request.PurchaseItemRequest;

/**
 * Shop 요청 DTO를 Application Command DTO로 변환하는 매퍼
 */
@Component
public class ShopRequestMapper {

    /**
     * PurchaseItemRequest를 PurchaseItemCommand로 변환합니다.
     *
     * @param request PurchaseItemRequest
     * @return PurchaseItemCommand
     */
    public PurchaseItemCommand toPurchaseItemCommand(PurchaseItemRequest request) {
        return PurchaseItemCommand.builder()
            .characterId(request.characterId())
            .itemId(request.itemId())
            .paymentMethod(request.paymentMethod())
            .build();
    }
}