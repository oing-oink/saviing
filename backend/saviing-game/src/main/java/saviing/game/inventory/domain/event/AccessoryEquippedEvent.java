package saviing.game.inventory.domain.event;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 액세서리 장착/해제 이벤트
 * Pet 도메인에서 액세서리 장착/해제 완료 시 발행됩니다.
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessoryEquippedEvent implements InventoryDomainEvent {
    private InventoryItemId accessoryInventoryItemId;
    private InventoryItemId petInventoryItemId;
    private CharacterId characterId;
    private boolean equipped;
    private LocalDateTime occurredAt;

    private AccessoryEquippedEvent(
        InventoryItemId accessoryInventoryItemId,
        InventoryItemId petInventoryItemId,
        CharacterId characterId,
        boolean equipped,
        LocalDateTime occurredAt
    ) {
        this.accessoryInventoryItemId = accessoryInventoryItemId;
        this.petInventoryItemId = petInventoryItemId;
        this.characterId = characterId;
        this.equipped = equipped;
        this.occurredAt = occurredAt;
    }

    /**
     * 액세서리 장착 이벤트를 생성합니다.
     *
     * @param accessoryInventoryItemId 액세서리 인벤토리 아이템 ID
     * @param petInventoryItemId 펫 인벤토리 아이템 ID
     * @param characterId 캐릭터 ID
     * @return AccessoryEquippedEvent 인스턴스
     */
    public static AccessoryEquippedEvent equip(
        InventoryItemId accessoryInventoryItemId,
        InventoryItemId petInventoryItemId,
        CharacterId characterId
    ) {
        return new AccessoryEquippedEvent(
            accessoryInventoryItemId,
            petInventoryItemId,
            characterId,
            true,
            LocalDateTime.now()
        );
    }

    /**
     * 액세서리 해제 이벤트를 생성합니다.
     *
     * @param accessoryInventoryItemId 액세서리 인벤토리 아이템 ID
     * @param characterId 캐릭터 ID
     * @return AccessoryEquippedEvent 인스턴스
     */
    public static AccessoryEquippedEvent unequip(
        InventoryItemId accessoryInventoryItemId,
        CharacterId characterId
    ) {
        return new AccessoryEquippedEvent(
            accessoryInventoryItemId,
            null,
            characterId,
            false,
            LocalDateTime.now()
        );
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredAt;
    }
}