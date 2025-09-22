package saviing.game.pet.application.dto.command;

import lombok.Builder;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 펫 상호작용 Command
 * 펫과의 상호작용 (놀아주기, 먹이주기 등)을 위한 명령 객체입니다.
 */
@Builder
public record InteractWithPetCommand(
    InventoryItemId inventoryItemId,
    int energyCost,
    int affectionGain
) {
    public InteractWithPetCommand {
        if (inventoryItemId == null) {
            throw new IllegalArgumentException("인벤토리 아이템 ID는 null일 수 없습니다");
        }
        if (energyCost < 0) {
            throw new IllegalArgumentException("포만감 소모량은 음수일 수 없습니다");
        }
        if (affectionGain < 0) {
            throw new IllegalArgumentException("애정도 증가량은 음수일 수 없습니다");
        }
    }

    /**
     * InteractWithPetCommand를 생성합니다.
     *
     * @param inventoryItemId 펫의 인벤토리 아이템 ID
     * @param energyCost 소모할 포만감
     * @param affectionGain 증가할 애정도
     * @return InteractWithPetCommand 인스턴스
     */
    public static InteractWithPetCommand of(InventoryItemId inventoryItemId, int energyCost, int affectionGain) {
        return InteractWithPetCommand.builder()
            .inventoryItemId(inventoryItemId)
            .energyCost(energyCost)
            .affectionGain(affectionGain)
            .build();
    }
}