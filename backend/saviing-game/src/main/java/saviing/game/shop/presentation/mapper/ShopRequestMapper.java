package saviing.game.shop.presentation.mapper;

import org.springframework.stereotype.Component;
import saviing.game.shop.application.dto.command.PurchaseItemCommand;
import saviing.game.shop.domain.model.vo.PaymentMethod;
import saviing.game.shop.presentation.dto.request.PurchaseItemRequest;
import saviing.game.shop.application.dto.command.DrawGachaCommand;
import saviing.game.shop.presentation.dto.request.GachaDrawRequest;

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
            .paymentMethod(parsePaymentMethod(request.paymentMethod()))
            .build();
    }

    /**
     * GachaDrawRequest를 DrawGachaCommand로 변환합니다.
     *
     * @param request GachaDrawRequest
     * @return DrawGachaCommand
     */
    public DrawGachaCommand toDrawGachaCommand(GachaDrawRequest request) {
        return DrawGachaCommand.builder()
            .characterId(request.characterId())
            .gachaPoolId(request.gachaPoolId())
            .paymentMethod(parsePaymentMethod(request.paymentMethod()))
            .build();
    }

    private PaymentMethod parsePaymentMethod(String paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }

        return PaymentMethod.from(paymentMethod);
    }
}
