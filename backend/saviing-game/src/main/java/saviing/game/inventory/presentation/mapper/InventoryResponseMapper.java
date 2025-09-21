package saviing.game.inventory.presentation.mapper;

import java.util.List;

import saviing.game.inventory.application.dto.result.InventoryListResult;
import saviing.game.inventory.application.dto.result.InventoryResult;
import saviing.game.inventory.presentation.dto.response.InventoryItemResponse;
import saviing.game.inventory.presentation.dto.response.InventoryListResponse;

import org.springframework.stereotype.Component;

/**
 * 조회 결과를 API 응답 DTO로 변환하는 매퍼입니다.
 */
@Component
public class InventoryResponseMapper {

    /**
     * 단일 인벤토리 결과를 응답 DTO로 변환합니다.
     */
    public InventoryItemResponse toResponse(InventoryResult result) {
        if (result == null) {
            return null;
        }

        return InventoryItemResponse.builder()
            .inventoryItemId(result.inventoryItemId())
            .characterId(result.characterId())
            .itemId(result.itemId())
            .type(result.type())
            .isUsed(result.isUsed())
            .name(result.itemName())
            .description(result.itemDescription())
            .category(result.category())
            .image(result.imageUrl())
            .rarity(result.rarity())
            .xLength(result.xLength())
            .yLength(result.yLength())
            .roomId(result.roomId())
            .petInventoryItemId(result.petInventoryItemId())
            .count(result.count())
            .createdAt(result.createdAt())
            .updatedAt(result.updatedAt())
            .build();
    }

    /**
     * 인벤토리 목록 결과를 응답 DTO로 변환합니다.
     */
    public InventoryListResponse toResponse(InventoryListResult result) {
        if (result == null) {
            return null;
        }

        List<InventoryItemResponse> inventories = result.inventories().stream()
            .map(this::toResponse)
            .toList();

        return new InventoryListResponse(inventories);
    }
}