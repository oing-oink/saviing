package saviing.game.inventory.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

import java.util.List;

/**
 * 인벤토리 아이템 다중 배치 Command
 * Room에서 여러 아이템을 한 번에 배치할 때 사용됩니다.
 */
@Builder
public record PlaceInventoryItemsCommand(
    List<InventoryItemId> inventoryItemIds,
    CharacterId characterId,
    Long roomId
) {

    /**
     * 단일 인벤토리 아이템 배치 Command를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @param characterId 캐릭터 ID
     * @param roomId 배치된 방 ID
     * @return PlaceInventoryItemsCommand 인스턴스
     */
    public static PlaceInventoryItemsCommand single(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        Long roomId
    ) {
        return PlaceInventoryItemsCommand.builder()
            .inventoryItemIds(List.of(inventoryItemId))
            .characterId(characterId)
            .roomId(roomId)
            .build();
    }

    /**
     * 다중 인벤토리 아이템 배치 Command를 생성합니다.
     *
     * @param inventoryItemIds 인벤토리 아이템 ID 목록
     * @param characterId 캐릭터 ID
     * @param roomId 배치된 방 ID
     * @return PlaceInventoryItemsCommand 인스턴스
     */
    public static PlaceInventoryItemsCommand multiple(
        List<InventoryItemId> inventoryItemIds,
        CharacterId characterId,
        Long roomId
    ) {
        return PlaceInventoryItemsCommand.builder()
            .inventoryItemIds(inventoryItemIds)
            .characterId(characterId)
            .roomId(roomId)
            .build();
    }

    /**
     * Command 유효성을 검증합니다.
     */
    public void validate() {
        if (inventoryItemIds == null || inventoryItemIds.isEmpty()) {
            throw new IllegalArgumentException("인벤토리 아이템 ID 목록은 필수입니다");
        }
        if (characterId == null) {
            throw new IllegalArgumentException("캐릭터 ID는 필수입니다");
        }
        if (roomId == null || roomId <= 0) {
            throw new IllegalArgumentException("방 ID는 1 이상이어야 합니다");
        }
    }
}