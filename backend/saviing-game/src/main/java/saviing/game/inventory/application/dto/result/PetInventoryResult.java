package saviing.game.inventory.application.dto.result;

import lombok.Builder;
import saviing.game.inventory.domain.model.enums.InventoryType;

import java.time.LocalDateTime;

/**
 * 펫 인벤토리 조회 결과 Result입니다.
 *
 * @param inventoryItemId 인벤토리 아이템 ID
 * @param characterId 캐릭터 ID
 * @param itemId 아이템 ID
 * @param type 인벤토리 타입
 * @param isUsed 사용 여부
 * @param roomId 배치된 방 ID
 * @param petName 펫 이름
 * @param level 레벨
 * @param experience 경험치
 * @param affection 애정도
 * @param energy 에너지
 * @param hatAccessoryId 모자 액세서리 ID
 * @param createdAt 생성 일시
 * @param updatedAt 최종 수정 일시
 */
@Builder
public record PetInventoryResult(
    Long inventoryItemId,
    Long characterId,
    Long itemId,
    InventoryType type,
    Boolean isUsed,
    Long roomId,
    String petName,
    Integer level,
    Integer experience,
    Integer affection,
    Integer energy,
    Long hatAccessoryId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}