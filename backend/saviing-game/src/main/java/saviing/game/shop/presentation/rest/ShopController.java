package saviing.game.shop.presentation.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import saviing.common.response.ApiResult;
import saviing.game.shop.application.dto.command.PurchaseItemCommand;
import saviing.game.shop.application.dto.result.PurchaseResult;
import saviing.game.shop.application.service.ShopCommandService;
import saviing.game.shop.application.service.ShopQueryService;
import saviing.game.shop.presentation.dto.request.PurchaseItemRequest;
import saviing.game.shop.presentation.dto.response.PurchaseResponse;
import saviing.game.shop.presentation.interfaces.ShopApi;
import saviing.game.shop.presentation.mapper.ShopRequestMapper;
import saviing.game.shop.presentation.mapper.ShopResponseMapper;
import saviing.game.shop.application.dto.command.DrawGachaCommand;
import saviing.game.shop.application.dto.query.GetGachaInfoQuery;
import saviing.game.shop.application.dto.result.GachaInfoResult;
import saviing.game.shop.application.dto.result.GachaDrawResult;
import saviing.game.shop.presentation.dto.request.GachaDrawRequest;
import saviing.game.shop.presentation.dto.response.GachaInfoResponse;
import saviing.game.item.domain.model.enums.Rarity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Shop REST API 컨트롤러
 * /v1/game/shop 경로로 상점 관련 API를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/v1/game/shop")
@RequiredArgsConstructor
public class ShopController implements ShopApi {

    private final ShopCommandService shopCommandService;
    private final ShopQueryService shopQueryService;
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
        PurchaseResult result = shopCommandService.requestItemPurchase(command);
        PurchaseResponse response = responseMapper.toPurchaseResponse(result);

        log.info("구매 요청 처리 완료: itemId={}", request.itemId());
        return ApiResult.ok(response);
    }

    /**
     * 가챠 정보를 조회합니다.
     *
     * @return 가챠 정보 응답
     */
    @GetMapping("/gacha/info")
    public ApiResult<GachaInfoResponse> getGachaInfo() {
        log.info("가챠 정보 조회 요청");

        GetGachaInfoQuery query = GetGachaInfoQuery.activeOnly();
        GachaInfoResult result = shopQueryService.getGachaInfo(query);
        GachaInfoResponse response = mapToGachaInfoResponse(result);

        log.info("가챠 정보 조회 완료: 가챠풀 {} ({})", response.gachaPoolId(), response.gachaPoolName());
        return ApiResult.ok(response);
    }

    /**
     * 가챠 뽑기를 실행합니다.
     *
     * @param request 가챠 뽑기 요청
     * @return 가챠 뽑기 결과
     */
    @PostMapping("/gacha/draw")
    public ApiResult<PurchaseResponse> drawGacha(@RequestBody GachaDrawRequest request) {
        log.info("가챠 뽑기 요청: characterId={}, gachaPoolId={}, paymentMethod={}",
            request.characterId(), request.gachaPoolId(), request.paymentMethod());

        request.validate();

        DrawGachaCommand command = mapToDrawGachaCommand(request);
        GachaDrawResult result = shopCommandService.drawGacha(command);
        PurchaseResponse response = mapToGachaDrawResponse(result);

        log.info("가챠 뽑기 처리 완료: characterId={}, drawnItem={}",
            request.characterId(), response.item().itemName());
        return ApiResult.ok(response);
    }

    /**
     * GachaInfoResult를 GachaInfoResponse로 매핑합니다.
     */
    private GachaInfoResponse mapToGachaInfoResponse(GachaInfoResult result) {
        return GachaInfoResponse.builder()
            .gachaPoolId(result.gachaPoolId())
            .gachaPoolName(result.gachaPoolName())
            .gachaInfo(GachaInfoResponse.GachaInfo.builder()
                .drawPrice(GachaInfoResponse.PriceResponse.builder()
                    .coin(result.gachaInfo().drawPrice().coin())
                    .fishCoin(result.gachaInfo().drawPrice().fishCoin())
                    .build())
                .dropRates(result.gachaInfo().dropRates())
                .rewardItemIds(convertRewardItemIds(result.gachaInfo().rewardItemIds()))
                .build())
            .build();
    }

    /**
     * 가챠 보상 아이템 정보를 응답 형태로 변환합니다.
     */
    private Map<Rarity, List<GachaInfoResponse.ItemResponse>> convertRewardItemIds(
        Map<Rarity, List<GachaInfoResult.ItemInfo>> rewardItemIds) {
        return rewardItemIds.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .map(itemInfo -> GachaInfoResponse.ItemResponse.builder()
                        .itemId(itemInfo.itemId())
                        .itemName(itemInfo.itemName())
                        .build())
                    .collect(Collectors.toList())
            ));
    }

    /**
     * GachaDrawRequest를 DrawGachaCommand로 매핑합니다.
     */
    private DrawGachaCommand mapToDrawGachaCommand(GachaDrawRequest request) {
        return requestMapper.toDrawGachaCommand(request);
    }

    /**
     * GachaDrawResult를 구매 응답으로 매핑합니다.
     */
    private PurchaseResponse mapToGachaDrawResponse(GachaDrawResult result) {
        return responseMapper.toGachaDrawResponse(result);
    }
}
