package saviing.game.item.domain.event;

import saviing.game.character.domain.event.DomainEvent;
import saviing.game.item.domain.model.vo.ItemId;

import java.time.LocalDateTime;

/**
 * 아이템 등록 도메인 이벤트
 * 새로운 아이템이 카탈로그에 등록될 때 발생합니다.
 */
public record ItemRegisteredEvent(
    ItemId itemId,
    String itemName,
    String itemType,
    String itemCategory,
    LocalDateTime occurredOn
) implements DomainEvent {

    /**
     * ItemRegisteredEvent를 생성합니다.
     *
     * @param itemId 등록된 아이템 ID
     * @param itemName 아이템 이름
     * @param itemType 아이템 타입
     * @param itemCategory 아이템 카테고리
     * @return ItemRegisteredEvent 인스턴스
     */
    public static ItemRegisteredEvent of(ItemId itemId, String itemName, String itemType, String itemCategory) {
        return new ItemRegisteredEvent(
            itemId,
            itemName,
            itemType,
            itemCategory,
            LocalDateTime.now()
        );
    }
}