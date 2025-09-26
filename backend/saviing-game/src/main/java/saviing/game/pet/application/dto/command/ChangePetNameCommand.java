package saviing.game.pet.application.dto.command;

import lombok.Builder;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 펫 이름 변경 Command
 * 펫의 이름을 변경하기 위한 명령 객체입니다.
 */
@Builder
public record ChangePetNameCommand(
    InventoryItemId inventoryItemId,
    String newName
) {
    public ChangePetNameCommand {
        if (inventoryItemId == null) {
            throw new IllegalArgumentException("인벤토리 아이템 ID는 null일 수 없습니다");
        }
        if (newName == null) {
            throw new IllegalArgumentException("새 이름은 null일 수 없습니다");
        }
    }

    /**
     * ChangePetNameCommand를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @param newName 새로운 펫 이름
     * @return ChangePetNameCommand 인스턴스
     */
    public static ChangePetNameCommand of(InventoryItemId inventoryItemId, String newName) {
        return ChangePetNameCommand.builder()
            .inventoryItemId(inventoryItemId)
            .newName(newName)
            .build();
    }
}