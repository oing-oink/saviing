package saviing.game.inventory.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 액세서리 장착/해제 Command
 * Pet에서 액세서리 장착/해제 시 inventory의 장착 정보를 업데이트할 때 사용됩니다.
 */
@Builder
public record EquipAccessoryCommand(
    InventoryItemId accessoryInventoryItemId,
    InventoryItemId petInventoryItemId,
    CharacterId characterId,
    boolean equip
) {

    /**
     * 액세서리 장착 Command를 생성합니다.
     *
     * @param accessoryInventoryItemId 액세서리 인벤토리 아이템 ID
     * @param petInventoryItemId 펫 인벤토리 아이템 ID
     * @param characterId 캐릭터 ID
     * @return EquipAccessoryCommand 인스턴스
     */
    public static EquipAccessoryCommand equip(
        InventoryItemId accessoryInventoryItemId,
        InventoryItemId petInventoryItemId,
        CharacterId characterId
    ) {
        return EquipAccessoryCommand.builder()
            .accessoryInventoryItemId(accessoryInventoryItemId)
            .petInventoryItemId(petInventoryItemId)
            .characterId(characterId)
            .equip(true)
            .build();
    }

    /**
     * 액세서리 해제 Command를 생성합니다.
     *
     * @param accessoryInventoryItemId 액세서리 인벤토리 아이템 ID
     * @param characterId 캐릭터 ID
     * @return EquipAccessoryCommand 인스턴스
     */
    public static EquipAccessoryCommand unequip(
        InventoryItemId accessoryInventoryItemId,
        CharacterId characterId
    ) {
        return EquipAccessoryCommand.builder()
            .accessoryInventoryItemId(accessoryInventoryItemId)
            .petInventoryItemId(null)
            .characterId(characterId)
            .equip(false)
            .build();
    }

    /**
     * Command 유효성을 검증합니다.
     */
    public void validate() {
        if (accessoryInventoryItemId == null) {
            throw new IllegalArgumentException("액세서리 인벤토리 아이템 ID는 필수입니다");
        }
        if (characterId == null) {
            throw new IllegalArgumentException("캐릭터 ID는 필수입니다");
        }
        if (equip && petInventoryItemId == null) {
            throw new IllegalArgumentException("장착 시 펫 인벤토리 아이템 ID는 필수입니다");
        }
    }
}