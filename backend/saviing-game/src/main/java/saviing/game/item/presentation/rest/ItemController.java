package saviing.game.item.presentation.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import saviing.common.response.ApiResult;
import saviing.game.item.application.dto.query.GetItemQuery;
import saviing.game.item.application.dto.query.GetListItemsQuery;
import saviing.game.item.application.dto.result.ItemListResult;
import saviing.game.item.application.dto.result.ItemResult;
import saviing.game.item.application.service.ItemQueryService;
import saviing.game.item.presentation.dto.response.ItemListResponse;
import saviing.game.item.presentation.dto.response.ItemResponse;
import saviing.game.item.presentation.interfaces.ItemApi;
import saviing.game.item.presentation.mapper.ItemResponseMapper;
import saviing.game.item.presentation.mapper.ItemRequestMapper;

/**
 * 아이템 REST API 컨트롤러
 * /v1/game/items 경로로 아이템 관련 API를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/v1/game")
@RequiredArgsConstructor
public class ItemController implements ItemApi {

    private final ItemQueryService itemQueryService;
    private final ItemResponseMapper itemResponseMapper;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @GetMapping("/items")
    public ApiResult<ItemListResponse> getItems(
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String rarity,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Boolean available,
        @RequestParam(defaultValue = "name") String sort,
        @RequestParam(defaultValue = "asc") String order,
        @RequestParam(required = false) String coinType
    ) {
        log.info("아이템 목록 조회 요청: type={}, category={}, rarity={}, sort={}, order={}",
            type, category, rarity, sort, order);

        GetListItemsQuery query = itemRequestMapper.toQuery(
            type, category, rarity, keyword, available, sort, order, coinType
        );

        ItemListResult result = itemQueryService.listItems(query);

        ItemListResponse response = itemResponseMapper.toResponse(result);

        log.info("아이템 목록 조회 완료: 총 {}개", response.totalCount());
        return ApiResult.ok(response);
    }

    @Override
    @GetMapping("/items/{itemId}")
    public ApiResult<ItemResponse> getItem(@PathVariable Long itemId) {
        log.info("아이템 조회 요청: itemId={}", itemId);

        GetItemQuery query = itemRequestMapper.toQuery(itemId);

        ItemResult result = itemQueryService.getItem(query);
        ItemResponse response = itemResponseMapper.toResponse(result);

        log.info("아이템 조회 완료: itemId={}", itemId);
        return ApiResult.ok(response);
    }

}