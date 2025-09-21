package saviing.game.inventory.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 인벤토리 아이템 소모 Command
 * Pet에서 소모품 사용 시 inventory의 개수를 감소시킬 때 사용됩니다.
 */
@Builder
public record ConsumeInventoryItemCommand(
    InventoryItemId inventoryItemId,
    CharacterId characterId,
    Integer consumedQuantity
) {

    /**
     * 인벤토리 아이템 소모 Command를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @param characterId 캐릭터 ID
     * @param consumedQuantity 소모된 개수
     * @return ConsumeInventoryItemCommand 인스턴스
     */
    public static ConsumeInventoryItemCommand of(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        Integer consumedQuantity
    ) {
        return ConsumeInventoryItemCommand.builder()
            .inventoryItemId(inventoryItemId)
            .characterId(characterId)
            .consumedQuantity(consumedQuantity)
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
        if (consumedQuantity == null || consumedQuantity <= 0) {
            throw new IllegalArgumentException("소모 개수는 1 이상이어야 합니다");
        }
    }
}