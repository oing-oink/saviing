package saviing.game.item.domain.event;

import saviing.game.character.domain.event.DomainEvent;
import saviing.game.item.domain.model.vo.ItemId;

import java.time.LocalDateTime;

/**
 * 아이템 가용성 변경 도메인 이벤트
 * 아이템의 판매 가능 상태가 변경될 때 발생합니다.
 */
public record ItemAvailabilityChangedEvent(
    ItemId itemId,
    String itemName,
    boolean isAvailable,
    String reason,
    LocalDateTime occurredOn
) implements DomainEvent {

    /**
     * ItemAvailabilityChangedEvent를 생성합니다.
     *
     * @param itemId 상태가 변경된 아이템 ID
     * @param itemName 아이템 이름
     * @param isAvailable 새로운 가용성 상태
     * @param reason 변경 사유
     * @return ItemAvailabilityChangedEvent 인스턴스
     */
    public static ItemAvailabilityChangedEvent of(ItemId itemId, String itemName, boolean isAvailable, String reason) {
        return new ItemAvailabilityChangedEvent(
            itemId,
            itemName,
            isAvailable,
            reason,
            LocalDateTime.now()
        );
    }

    /**
     * 아이템이 판매 가능하게 된 경우의 이벤트를 생성합니다.
     *
     * @param itemId 아이템 ID
     * @param itemName 아이템 이름
     * @return ItemAvailabilityChangedEvent 인스턴스
     */
    public static ItemAvailabilityChangedEvent makeAvailable(ItemId itemId, String itemName) {
        return of(itemId, itemName, true, "아이템 판매 시작");
    }

    /**
     * 아이템이 판매 불가능하게 된 경우의 이벤트를 생성합니다.
     *
     * @param itemId 아이템 ID
     * @param itemName 아이템 이름
     * @param reason 판매 중단 사유
     * @return ItemAvailabilityChangedEvent 인스턴스
     */
    public static ItemAvailabilityChangedEvent makeUnavailable(ItemId itemId, String itemName, String reason) {
        return of(itemId, itemName, false, reason);
    }

    /**
     * 아이템이 판매 가능한 상태로 변경되었는지 확인합니다.
     *
     * @return 판매 가능 상태로 변경되었는지 여부
     */
    public boolean isBecomingAvailable() {
        return isAvailable;
    }

    /**
     * 아이템이 판매 불가능한 상태로 변경되었는지 확인합니다.
     *
     * @return 판매 불가능 상태로 변경되었는지 여부
     */
    public boolean isBecomingUnavailable() {
        return !isAvailable;
    }
}