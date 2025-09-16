package saviing.game.item.presentation.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import saviing.common.response.ApiResult;
import saviing.game.item.application.dto.result.ItemResult;
import saviing.game.item.application.service.ItemCommandService;
import saviing.game.item.presentation.dto.request.ChangeAvailabilityRequest;
import saviing.game.item.presentation.dto.request.CreateItemRequest;
import saviing.game.item.presentation.dto.request.UpdateItemRequest;
import saviing.game.item.presentation.dto.response.ItemResponse;
import saviing.game.item.presentation.mapper.ItemCommandRequestMapper;
import saviing.game.item.presentation.mapper.ItemResponseMapper;

/**
 * 아이템 테스트용 REST API 컨트롤러
 * 개발/테스트 환경에서 아이템 CRUD 작업을 위한 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping("/v1/test/items")
@RequiredArgsConstructor
@Tag(name = "ItemTest", description = "아이템 테스트 관리 API")
public class ItemTestController {

    private final ItemCommandService itemCommandService;
    private final ItemCommandRequestMapper requestMapper;
    private final ItemResponseMapper responseMapper;

    @Operation(summary = "아이템 생성", description = "새로운 아이템을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping
    public ApiResult<ItemResponse> createItem(@Valid @RequestBody CreateItemRequest request) {
        log.info("아이템 생성 요청: {}", request.itemName());

        ItemResult result = itemCommandService.registerItem(requestMapper.toCommand(request));
        ItemResponse response = responseMapper.toResponse(result);

        return ApiResult.of(HttpStatus.CREATED, response);
    }

    @Operation(summary = "아이템 수정", description = "기존 아이템의 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping("/{itemId}")
    public ApiResult<ItemResponse> updateItem(
        @PathVariable Long itemId,
        @Valid @RequestBody UpdateItemRequest request
    ) {
        log.info("아이템 수정 요청: itemId={}, name={}", itemId, request.itemName());

        ItemResult result = itemCommandService.updateItem(requestMapper.toCommand(itemId, request));
        ItemResponse response = responseMapper.toResponse(result);

        return ApiResult.ok(response);
    }

    @Operation(summary = "아이템 가용성 변경", description = "아이템의 판매 가용성을 변경합니다.")
    @ApiResponse(responseCode = "200", description = "변경 성공")
    @PatchMapping("/{itemId}/availability")
    public ApiResult<Void> changeAvailability(
        @PathVariable Long itemId,
        @Valid @RequestBody ChangeAvailabilityRequest request
    ) {
        log.info("아이템 가용성 변경 요청: itemId={}, available={}", itemId, request.isAvailable());

        itemCommandService.changeAvailability(requestMapper.toCommand(itemId, request));

        return ApiResult.ok();
    }

    @Operation(summary = "아이템 삭제", description = "아이템을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{itemId}")
    public ApiResult<Void> deleteItem(@PathVariable Long itemId) {
        log.info("아이템 삭제 요청: itemId={}", itemId);

        itemCommandService.deleteItem(itemId);

        return ApiResult.ok();
    }
}