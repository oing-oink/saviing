package saviing.game.pet.application.dto.command;

import lombok.Builder;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 펫 생성 Command
 * PET 아이템 구매 시 펫 정보를 생성하기 위한 명령 객체입니다.
 */
@Builder
public record CreatePetCommand(
    InventoryItemId inventoryItemId
) {
    public CreatePetCommand {
        if (inventoryItemId == null) {
            throw new IllegalArgumentException("인벤토리 아이템 ID는 null일 수 없습니다");
        }
    }

    /**
     * CreatePetCommand를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @return CreatePetCommand 인스턴스
     */
    public static CreatePetCommand of(InventoryItemId inventoryItemId) {
        return CreatePetCommand.builder()
            .inventoryItemId(inventoryItemId)
            .build();
    }
}