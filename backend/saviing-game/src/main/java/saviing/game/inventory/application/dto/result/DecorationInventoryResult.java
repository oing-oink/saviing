package saviing.game.inventory.application.dto.result;

import lombok.Builder;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.item.domain.model.enums.Decoration;

import java.time.LocalDateTime;

/**
 * 데코레이션 인벤토리 조회 결과 Result입니다.
 *
 * @param inventoryItemId 인벤토리 아이템 ID
 * @param characterId 캐릭터 ID
 * @param itemId 아이템 ID
 * @param type 인벤토리 타입
 * @param isUsed 사용 여부
 * @param category 데코레이션 카테고리
 * @param roomId 배치된 방 ID
 * @param xPosition X 좌표
 * @param yPosition Y 좌표
 * @param createdAt 생성 일시
 * @param updatedAt 최종 수정 일시
 */
@Builder
public record DecorationInventoryResult(
    Long inventoryItemId,
    Long characterId,
    Long itemId,
    InventoryType type,
    Boolean isUsed,
    Decoration category,
    Long roomId,
    Integer xPosition,
    Integer yPosition,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}