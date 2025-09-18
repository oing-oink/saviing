package saviing.game.shop.presentation.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import saviing.common.response.ApiResult;
import saviing.game.shop.presentation.dto.request.PurchaseItemRequest;
import saviing.game.shop.presentation.dto.response.PurchaseResponse;


/**
 * Shop API 인터페이스
 */
@Tag(name = "Shop", description = "Shop API")
public interface ShopApi {

    @Operation(
        summary = "아이템 구매",
        description = "캐릭터가 아이템을 구매합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "구매 요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "아이템 또는 캐릭터를 찾을 수 없음")
    })
    @PostMapping("/purchase")
    ApiResult<PurchaseResponse> purchaseItem(
        @RequestBody PurchaseItemRequest request
    );

}