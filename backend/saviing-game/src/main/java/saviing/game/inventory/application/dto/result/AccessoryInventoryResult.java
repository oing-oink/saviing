package saviing.game.inventory.application.dto.result;

import lombok.Builder;
import saviing.game.inventory.domain.model.enums.InventoryType;

import java.time.LocalDateTime;

/**
 * 액세서리 인벤토리 조회 결과 Result입니다.
 *
 * @param inventoryItemId 인벤토리 아이템 ID
 * @param characterId 캐릭터 ID
 * @param itemId 아이템 ID
 * @param type 인벤토리 타입
 * @param isUsed 사용 여부
 * @param category 액세서리 카테고리
 * @param petInventoryItemId 장착된 펫 ID (장착 중일 때만)
 * @param createdAt 생성 일시
 * @param updatedAt 최종 수정 일시
 */
@Builder
public record AccessoryInventoryResult(
    Long inventoryItemId,
    Long characterId,
    Long itemId,
    InventoryType type,
    Boolean isUsed,
    String category,
    Long petInventoryItemId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}