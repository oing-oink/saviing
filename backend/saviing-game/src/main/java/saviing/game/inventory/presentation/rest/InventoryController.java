package saviing.game.inventory.presentation.rest;

import saviing.common.response.ApiResult;
import saviing.game.inventory.application.dto.query.GetInventoriesByCharacterQuery;
import saviing.game.inventory.application.dto.query.GetInventoryQuery;
import saviing.game.inventory.application.dto.result.InventoryListResult;
import saviing.game.inventory.application.dto.result.InventoryResult;
import saviing.game.inventory.application.service.InventoryQueryService;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.enums.ItemCategory;
import saviing.game.inventory.presentation.dto.response.InventoryItemResponse;
import saviing.game.inventory.presentation.dto.response.InventoryListResponse;
import saviing.game.inventory.presentation.interfaces.InventoryApi;
import saviing.game.inventory.presentation.mapper.InventoryResponseMapper;
import saviing.game.inventory.presentation.mapper.InventoryRequestMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인벤토리 조회 API를 제공하는 REST 컨트롤러입니다.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/game/inventory")
public class InventoryController implements InventoryApi {

    private final InventoryQueryService inventoryQueryService;
    private final InventoryResponseMapper responseMapper;
    private final InventoryRequestMapper requestMapper;

    @Override
    @GetMapping("/characters/{characterId}")
    public ApiResult<InventoryListResponse> getInventory(
        @PathVariable Long characterId,
        @RequestParam(required = false) InventoryType type,
        @RequestParam(required = false) ItemCategory category,
        @RequestParam(required = false) Boolean isUsed
    ) {
        log.info("캐릭터 인벤토리 조회 요청 - characterId={}, type={}, category={}, isUsed={}",
            characterId, type, category, isUsed);

        GetInventoriesByCharacterQuery query = requestMapper.toQuery(characterId, type, category, isUsed);

        InventoryListResult result = inventoryQueryService.getInventoriesByCharacter(query);
        InventoryListResponse response = responseMapper.toResponse(result);

        return ApiResult.ok(response);
    }

    @Override
    @GetMapping("/items/{inventoryItemId}")
    public ApiResult<InventoryItemResponse> getInventoryItem(@PathVariable Long inventoryItemId) {
        log.info("인벤토리 아이템 단건 조회 요청 - inventoryItemId={}", inventoryItemId);

        GetInventoryQuery query = GetInventoryQuery.of(inventoryItemId);

        InventoryResult result = inventoryQueryService.getInventory(query);
        InventoryItemResponse response = responseMapper.toResponse(result);

        return ApiResult.ok(response);
    }
}