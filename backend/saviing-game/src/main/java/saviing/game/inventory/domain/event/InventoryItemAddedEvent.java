package saviing.game.inventory.domain.event;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.item.domain.model.vo.ItemId;

/**
 * 인벤토리 아이템 추가 이벤트
 * 새로운 아이템이 인벤토리에 추가되었을 때 발행됩니다.
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryItemAddedEvent implements InventoryDomainEvent {
    private InventoryItemId inventoryItemId;
    private CharacterId characterId;
    private ItemId itemId;
    private InventoryType inventoryType;
    private LocalDateTime occurredAt;

    private InventoryItemAddedEvent(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        ItemId itemId,
        InventoryType inventoryType,
        LocalDateTime occurredAt
    ) {
        this.inventoryItemId = inventoryItemId;
        this.characterId = characterId;
        this.itemId = itemId;
        this.inventoryType = inventoryType;
        this.occurredAt = occurredAt;
    }

    /**
     * InventoryItemAddedEvent를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @param characterId 캐릭터 ID
     * @param itemId 아이템 ID
     * @param inventoryType 인벤토리 타입
     * @return InventoryItemAddedEvent 인스턴스
     */
    public static InventoryItemAddedEvent of(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        ItemId itemId,
        InventoryType inventoryType
    ) {
        return new InventoryItemAddedEvent(
            inventoryItemId,
            characterId,
            itemId,
            inventoryType,
            LocalDateTime.now()
        );
    }


    @Override
    public LocalDateTime occurredOn() {
        return occurredAt;
    }

}