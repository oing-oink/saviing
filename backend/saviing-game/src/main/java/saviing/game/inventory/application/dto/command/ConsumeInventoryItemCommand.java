package saviing.game.inventory.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 인벤토리 아이템 소모/증가 Command
 * 소모품의 개수를 증가시키거나 감소시킬 때 사용됩니다.
 */
@Builder
public record ConsumeInventoryItemCommand(
    InventoryItemId inventoryItemId,
    CharacterId characterId,
    Integer quantityChange
) {

    /**
     * 인벤토리 아이템 소모 Command를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @param characterId 캐릭터 ID
     * @param consumedQuantity 소모된 개수 (양수)
     * @return ConsumeInventoryItemCommand 인스턴스
     */
    public static ConsumeInventoryItemCommand consume(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        Integer consumedQuantity
    ) {
        return ConsumeInventoryItemCommand.builder()
            .inventoryItemId(inventoryItemId)
            .characterId(characterId)
            .quantityChange(-Math.abs(consumedQuantity))
            .build();
    }

    /**
     * 인벤토리 아이템 증가 Command를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @param characterId 캐릭터 ID
     * @param increaseQuantity 증가된 개수 (양수)
     * @return ConsumeInventoryItemCommand 인스턴스
     */
    public static ConsumeInventoryItemCommand increase(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        Integer increaseQuantity
    ) {
        return ConsumeInventoryItemCommand.builder()
            .inventoryItemId(inventoryItemId)
            .characterId(characterId)
            .quantityChange(Math.abs(increaseQuantity))
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
        if (quantityChange == null || quantityChange == 0) {
            throw new IllegalArgumentException("개수 변화량은 0이 아닌 값이어야 합니다");
        }
    }

    /**
     * 소모 작업인지 확인합니다.
     */
    public boolean isConsume() {
        return quantityChange < 0;
    }

    /**
     * 증가 작업인지 확인합니다.
     */
    public boolean isIncrease() {
        return quantityChange > 0;
    }
}