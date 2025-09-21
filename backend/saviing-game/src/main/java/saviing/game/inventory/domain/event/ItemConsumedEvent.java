package saviing.game.inventory.domain.event;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 소모품 사용 이벤트
 * 소모품이 사용되어 개수가 감소할 때 발행됩니다.
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemConsumedEvent implements InventoryDomainEvent {
    private InventoryItemId inventoryItemId;
    private CharacterId characterId;
    private Integer consumedQuantity;
    private LocalDateTime occurredAt;

    private ItemConsumedEvent(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        Integer consumedQuantity,
        LocalDateTime occurredAt
    ) {
        this.inventoryItemId = inventoryItemId;
        this.characterId = characterId;
        this.consumedQuantity = consumedQuantity;
        this.occurredAt = occurredAt;
    }

    /**
     * ItemConsumedEvent를 생성합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @param characterId 캐릭터 ID
     * @param consumedQuantity 소모된 개수
     * @return ItemConsumedEvent 인스턴스
     */
    public static ItemConsumedEvent of(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        Integer consumedQuantity
    ) {
        return new ItemConsumedEvent(
            inventoryItemId,
            characterId,
            consumedQuantity,
            LocalDateTime.now()
        );
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredAt;
    }
}