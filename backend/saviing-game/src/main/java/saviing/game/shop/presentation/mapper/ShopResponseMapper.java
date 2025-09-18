package saviing.game.shop.presentation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import saviing.game.character.application.dto.result.CharacterResult;
import saviing.game.item.presentation.mapper.ItemResponseMapper;
import saviing.game.shop.application.dto.result.PurchaseResult;
import saviing.game.shop.presentation.dto.response.CurrencyInfo;
import saviing.game.shop.presentation.dto.response.PurchaseResponse;

/**
 * Shop 응답 DTO를 생성하는 매퍼입니다.
 * 아이템 정보와 캐릭터 잔액 정보를 조합하여 구매 응답을 생성합니다.
 */
@Component
@RequiredArgsConstructor
public class ShopResponseMapper {

    private final ItemResponseMapper itemResponseMapper;

    /**
     * PurchaseResult를 PurchaseResponse로 변환합니다.
     *
     * @param purchaseResult 구매 결과 정보
     * @return PurchaseResponse
     */
    public PurchaseResponse toPurchaseResponse(PurchaseResult purchaseResult) {
        return PurchaseResponse.builder()
            .item(itemResponseMapper.toResponse(purchaseResult.item()))
            .currencies(createCurrencyInfo(purchaseResult.character(), purchaseResult.paymentMethod()))
            .build();
    }

    /**
     * 결제 수단과 캐릭터 정보를 기반으로 CurrencyInfo를 생성합니다.
     *
     * @param characterResult 캐릭터 정보
     * @param paymentMethod 사용한 결제 수단
     * @return CurrencyInfo
     */
    private CurrencyInfo createCurrencyInfo(CharacterResult characterResult, String paymentMethod) {
        String coinType = "COIN".equals(paymentMethod) ? "COIN" : "FISH_COIN";
        Integer balance = "COIN".equals(paymentMethod) ?
            characterResult.coin() : characterResult.fishCoin();

        return CurrencyInfo.builder()
            .coinType(coinType)
            .balance(balance)
            .build();
    }
}