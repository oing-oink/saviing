package saviing.game.inventory.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 인벤토리 아이템 배치 Command
 * Room에서 아이템 배치 시 inventory의 위치 정보를 업데이트할 때 사용됩니다.
 */
@Builder
public record PlaceInventoryItemCommand(
    InventoryItemId inventoryItemId,
    CharacterId characterId,
    Long roomId
) {

    /**
     * 인벤토리 아이템 배치 Command를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @param characterId 캐릭터 ID
     * @param roomId 배치된 방 ID
     * @return PlaceInventoryItemCommand 인스턴스
     */
    public static PlaceInventoryItemCommand of(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        Long roomId
    ) {
        return PlaceInventoryItemCommand.builder()
            .inventoryItemId(inventoryItemId)
            .characterId(characterId)
            .roomId(roomId)
            .build();
    }

    /**
     * Command 유효성을 검증합니다.
     */
    public void validate() {
        if (inventoryItemId == null) {
            throw new IllegalArgumentException("인벤토리 아이템 ID는 필수입니다");
        }
        if (characterId == null) {
            throw new IllegalArgumentException("캐릭터 ID는 필수입니다");
        }
        if (roomId == null || roomId <= 0) {
            throw new IllegalArgumentException("방 ID는 1 이상이어야 합니다");
        }
    }
}