package saviing.game.pet.application.dto.command;

import lombok.Builder;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 펫 생성 Command
 * PET 아이템 구매 시 펫을 생성하기 위한 명령 객체입니다.
 */
@Builder
public record CreatePetCommand(
    InventoryItemId inventoryItemId,
    String itemName
) {
    public CreatePetCommand {
        if (inventoryItemId == null) {
            throw new IllegalArgumentException("인벤토리 아이템 ID는 null일 수 없습니다");
        }
        if (itemName == null) {
            throw new IllegalArgumentException("아이템 이름은 null일 수 없습니다");
        }
    }

    /**
     * CreatePetCommand를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @param itemName 아이템 이름 (펫의 기본 이름으로 사용)
     * @return CreatePetCommand 인스턴스
     */
    public static CreatePetCommand of(InventoryItemId inventoryItemId, String itemName) {
        return CreatePetCommand.builder()
            .inventoryItemId(inventoryItemId)
            .itemName(itemName)
            .build();
    }
}