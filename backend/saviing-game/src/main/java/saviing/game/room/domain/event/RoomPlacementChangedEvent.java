package saviing.game.room.domain.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import saviing.common.event.DomainEvent;
import saviing.game.room.domain.model.aggregate.PlacedItem;
import saviing.game.room.domain.model.vo.RoomId;

/**
 * 방 배치 변경 시 발생하는 도메인 이벤트
 * Inventory BC와의 동기화를 위해 배치 변경 정보를 전달함
 */
@Getter
public class RoomPlacementChangedEvent implements DomainEvent {

    private final RoomId roomId;
    private final List<PlacedItem> placedItems;
    private final LocalDateTime occurredAt;

    /**
     * RoomPlacementChangedEvent 생성자
     *
     * @param roomId 변경된 방의 식별자
     * @param placedItems 변경 후 배치된 아이템 목록
     * @param occurredAt 이벤트 발생 시각
     * @throws IllegalArgumentException roomId나 placedItems가 null인 경우
     */
    public RoomPlacementChangedEvent(RoomId roomId, List<PlacedItem> placedItems,
            LocalDateTime occurredAt) {
        this.roomId = Objects.requireNonNull(roomId, "roomId");
        this.placedItems = Objects.requireNonNull(placedItems, "placedItems");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    /**
     * 현재 시각으로 이벤트를 생성하는 정적 팩토리 메서드
     *
     * @param roomId 변경된 방의 식별자
     * @param placedItems 변경 후 배치된 아이템 목록
     * @return 생성된 RoomPlacementChangedEvent 인스턴스
     * @throws IllegalArgumentException roomId나 placedItems가 null인 경우
     */
    public static RoomPlacementChangedEvent of(RoomId roomId, List<PlacedItem> placedItems) {
        return new RoomPlacementChangedEvent(roomId, placedItems, LocalDateTime.now());
    }

    /**
     * 배치된 아이템 개수를 반환
     *
     * @return 배치된 아이템 개수
     */
    public int getPlacedItemCount() {
        return placedItems.size();
    }

    /**
     * 펫 아이템이 포함되어 있는지 확인
     *
     * @return 펫 아이템이 포함되어 있으면 true, 그렇지 않으면 false
     */
    public boolean containsPetItems() {
        return placedItems.stream().anyMatch(PlacedItem::isPet);
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredAt;
    }
}