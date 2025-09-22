package saviing.game.inventory.domain.event;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 아이템 배치 이벤트
 * 아이템이 방에 배치될 때 발행됩니다.
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemPlacedEvent implements InventoryDomainEvent {
    private InventoryItemId inventoryItemId;
    private CharacterId characterId;
    private InventoryType inventoryType;
    private Long roomId;
    private LocalDateTime occurredAt;

    private ItemPlacedEvent(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        InventoryType inventoryType,
        Long roomId,
        LocalDateTime occurredAt
    ) {
        this.inventoryItemId = inventoryItemId;
        this.characterId = characterId;
        this.inventoryType = inventoryType;
        this.roomId = roomId;
        this.occurredAt = occurredAt;
    }

    /**
     * ItemPlacedEvent를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @param characterId 캐릭터 ID
     * @param inventoryType 인벤토리 타입
     * @param roomId 배치된 방 ID
     * @return ItemPlacedEvent 인스턴스
     */
    public static ItemPlacedEvent of(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        InventoryType inventoryType,
        Long roomId
    ) {
        return new ItemPlacedEvent(
            inventoryItemId,
            characterId,
            inventoryType,
            roomId,
            LocalDateTime.now()
        );
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredAt;
    }
}