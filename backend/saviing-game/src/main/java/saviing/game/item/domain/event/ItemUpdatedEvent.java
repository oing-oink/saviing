package saviing.game.item.domain.event;

import saviing.game.item.domain.model.vo.ItemId;
import saviing.game.item.domain.model.vo.ItemName;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 아이템 업데이트 도메인 이벤트
 * 아이템의 속성이 수정될 때 발생합니다.
 */
public record ItemUpdatedEvent(
    ItemId itemId,
    ItemName itemName,
    Map<String, Object> changedFields,
    LocalDateTime occurredOn
) implements DomainEvent {

    /**
     * ItemUpdatedEvent를 생성합니다.
     *
     * @param itemId 업데이트된 아이템 ID
     * @param itemName 아이템 이름
     * @param changedFields 변경된 필드들 (필드명 -> 새로운 값)
     * @return ItemUpdatedEvent 인스턴스
     */
    public static ItemUpdatedEvent of(ItemId itemId, ItemName itemName, Map<String, Object> changedFields) {
        return new ItemUpdatedEvent(
            itemId,
            itemName,
            Map.copyOf(changedFields), // 불변 복사본 생성
            LocalDateTime.now()
        );
    }

    /**
     * 단일 필드 변경을 위한 ItemUpdatedEvent를 생성합니다.
     *
     * @param itemId 업데이트된 아이템 ID
     * @param itemName 아이템 이름
     * @param fieldName 변경된 필드명
     * @param newValue 새로운 값
     * @return ItemUpdatedEvent 인스턴스
     */
    public static ItemUpdatedEvent ofSingleField(ItemId itemId, ItemName itemName, String fieldName, Object newValue) {
        return new ItemUpdatedEvent(
            itemId,
            itemName,
            Map.of(fieldName, newValue),
            LocalDateTime.now()
        );
    }

    /**
     * 특정 필드가 변경되었는지 확인합니다.
     *
     * @param fieldName 확인할 필드명
     * @return 해당 필드가 변경되었는지 여부
     */
    public boolean hasChangedField(String fieldName) {
        return changedFields.containsKey(fieldName);
    }

    /**
     * 변경된 필드의 값을 반환합니다.
     *
     * @param fieldName 필드명
     * @return 변경된 값 (없으면 null)
     */
    public Object getChangedValue(String fieldName) {
        return changedFields.get(fieldName);
    }

}