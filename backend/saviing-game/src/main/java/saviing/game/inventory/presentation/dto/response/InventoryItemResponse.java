package saviing.game.inventory.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.item.domain.model.enums.Rarity;

import java.time.LocalDateTime;

/**
 * 인벤토리 아이템 조회 응답 DTO입니다.
 * 아이템 정보를 포함한 상세 정보를 제공합니다.
 */
@Builder
@Schema(description = "인벤토리 아이템 응답")
public record InventoryItemResponse(
    @Schema(description = "인벤토리 아이템 ID", example = "1")
    Long inventoryItemId,

    @Schema(description = "캐릭터 ID", example = "1")
    Long characterId,

    @Schema(description = "아이템 ID", example = "1001")
    Long itemId,

    @Schema(description = "인벤토리 유형", example = "PET")
    InventoryType type,

    @Schema(description = "사용 여부", example = "false")
    Boolean isUsed,

    @Schema(description = "아이템 이름", example = "우아한 벽지")
    String name,

    @Schema(description = "아이템 설명", example = "방을 꾸밀 수 있는 고급 벽지 아이템입니다.")
    String description,

    @Schema(description = "아이템 카테고리", example = "WALL")
    String itemCategory,

    @Schema(description = "아이템 이미지 URL", example = "wallpaper_luxury.png")
    String image,

    @Schema(description = "아이템 희귀도", example = "COMMON")
    Rarity rarity,

    @Schema(description = "아이템 가로 크기", example = "2")
    Integer xLength,

    @Schema(description = "아이템 세로 크기", example = "3")
    Integer yLength,

    @Schema(description = "방 ID (PET이나 DECORATION이 배치된 경우에만)", example = "1")
    Long roomId,

    @Schema(description = "펫 인벤토리 ID (ACCESSORY가 장착된 경우에만)", example = "5")
    Long petInventoryItemId,

    @Schema(description = "소모품 개수 (CONSUMPTION인 경우에만)", example = "3")
    Integer count,

    @Schema(description = "생성 시각", example = "2025-01-15T10:30:00")
    LocalDateTime createdAt,

    @Schema(description = "최종 수정 시각", example = "2025-01-15T12:00:00")
    LocalDateTime updatedAt
) {
}