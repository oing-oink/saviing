package saviing.game.shop.presentation.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import saviing.common.response.ApiResult;
import saviing.game.shop.application.dto.command.PurchaseItemCommand;
import saviing.game.shop.application.dto.result.PurchaseResult;
import saviing.game.shop.application.service.ShopService;
import saviing.game.shop.presentation.dto.request.PurchaseItemRequest;
import saviing.game.shop.presentation.dto.response.PurchaseResponse;
import saviing.game.shop.presentation.interfaces.ShopApi;
import saviing.game.shop.presentation.mapper.ShopRequestMapper;
import saviing.game.shop.presentation.mapper.ShopResponseMapper;

/**
 * Shop REST API 컨트롤러
 * /v1/game/shop 경로로 상점 관련 API를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/v1/game/shop")
@RequiredArgsConstructor
public class ShopController implements ShopApi {

    private final ShopService shopService;
    private final ShopRequestMapper requestMapper;
    private final ShopResponseMapper responseMapper;

    /**
     * 아이템 구매를 요청합니다.
     *
     * @param request 구매 요청
     * @return 구매 결과
     */
    @PostMapping("/purchase")
    public ApiResult<PurchaseResponse> purchaseItem(@RequestBody PurchaseItemRequest request) {
        log.info("아이템 구매 요청: characterId={}, itemId={}, paymentMethod={}",
            request.characterId(), request.itemId(), request.paymentMethod());

        PurchaseItemCommand command = requestMapper.toPurchaseItemCommand(request);
        PurchaseResult result = shopService.requestItemPurchase(command);
        PurchaseResponse response = responseMapper.toPurchaseResponse(result);

        log.info("구매 요청 처리 완료: itemId={}", request.itemId());
        return ApiResult.ok(response);
    }
}