package saviing.game.inventory.domain.event;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.item.domain.model.vo.ItemId;

/**
 * 사용안됨 - 직접 서비스 호출로 대체
 *
 * 아이템 구매 이벤트
 * Shop 도메인에서 아이템 구매 완료 시 발행됩니다.
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemPurchasedEvent implements InventoryDomainEvent {
    private CharacterId characterId;
    private ItemId itemId;
    private String itemName;
    private LocalDateTime occurredAt;

    private ItemPurchasedEvent(
        CharacterId characterId,
        ItemId itemId,
        String itemName,
        LocalDateTime occurredAt
    ) {
        this.characterId = characterId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.occurredAt = occurredAt;
    }

    /**
     * ItemPurchasedEvent를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param itemId 아이템 ID
     * @param itemName 아이템 이름
     * @return ItemPurchasedEvent 인스턴스
     */
    public static ItemPurchasedEvent of(
        CharacterId characterId,
        ItemId itemId,
        String itemName
    ) {
        return new ItemPurchasedEvent(
            characterId,
            itemId,
            itemName,
            LocalDateTime.now()
        );
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredAt;
    }
}