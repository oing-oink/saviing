package saviing.game.pet.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.pet.domain.model.enums.InteractionType;

/**
 * 펫 상호작용 Command
 * 펫과의 상호작용 (놀아주기, 먹이주기 등)을 위한 명령 객체입니다.
 */
@Builder
public record InteractWithPetCommand(
    CharacterId characterId,
    InventoryItemId inventoryItemId,
    InteractionType interactionType
) {
    public InteractWithPetCommand {
        if (characterId == null) {
            throw new IllegalArgumentException("캐릭터 ID는 null일 수 없습니다");
        }
        if (inventoryItemId == null) {
            throw new IllegalArgumentException("인벤토리 아이템 ID는 null일 수 없습니다");
        }
        if (interactionType == null) {
            throw new IllegalArgumentException("상호작용 타입은 null일 수 없습니다");
        }
    }

    /**
     * InteractWithPetCommand를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param inventoryItemId 펫의 인벤토리 아이템 ID
     * @param interactionType 상호작용 타입
     * @return InteractWithPetCommand 인스턴스
     */
    public static InteractWithPetCommand of(CharacterId characterId, InventoryItemId inventoryItemId, InteractionType interactionType) {
        return InteractWithPetCommand.builder()
            .characterId(characterId)
            .inventoryItemId(inventoryItemId)
            .interactionType(interactionType)
            .build();
    }
}