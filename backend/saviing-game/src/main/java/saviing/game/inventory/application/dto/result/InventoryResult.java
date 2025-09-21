package saviing.game.inventory.application.dto.result;

import lombok.Builder;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.item.domain.model.enums.Rarity;

import java.time.LocalDateTime;

/**
 * 인벤토리 조회 결과 Result입니다.
 * 아이템 정보를 포함한 상세 정보를 제공합니다.
 *
 * @param inventoryItemId 인벤토리 아이템 ID
 * @param characterId 캐릭터 ID
 * @param itemId 아이템 ID
 * @param type 인벤토리 타입
 * @param isUsed 사용 여부
 * @param itemName 아이템 이름
 * @param itemDescription 아이템 설명
 * @param category 아이템 카테고리
 * @param imageUrl 아이템 이미지 URL
 * @param rarity 아이템 희귀도
 * @param xLength 아이템 가로 크기
 * @param yLength 아이템 세로 크기
 * @param roomId 방 ID (사용 중일 때만)
 * @param createdAt 생성 일시
 * @param updatedAt 최종 수정 일시
 */
@Builder
public record InventoryResult(
    Long inventoryItemId,
    Long characterId,
    Long itemId,
    InventoryType type,
    Boolean isUsed,
    String itemName,
    String itemDescription,
    String category,
    String imageUrl,
    Rarity rarity,
    Integer xLength,
    Integer yLength,
    Long roomId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}