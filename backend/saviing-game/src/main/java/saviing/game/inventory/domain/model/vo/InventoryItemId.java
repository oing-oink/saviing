package saviing.game.inventory.domain.model.vo;

/**
 * 인벤토리 아이템 식별자 Value Object
 */
public record InventoryItemId(
    Long value
) {
    public InventoryItemId {
        if (value != null && value <= 0) {
            throw new IllegalArgumentException("인벤토리 아이템 ID는 양수여야 합니다");
        }
    }

    /**
     * Long 값으로 InventoryItemId를 생성합니다.
     *
     * @param value 인벤토리 아이템 ID 값
     * @return InventoryItemId 인스턴스
     */
    public static InventoryItemId of(Long value) {
        return new InventoryItemId(value);
    }

    /**
     * 새로운 InventoryItemId를 생성합니다 (ID가 아직 할당되지 않은 경우).
     *
     * @return null value를 가진 InventoryItemId 인스턴스
     */
    public static InventoryItemId newInstance() {
        return new InventoryItemId(null);
    }

    /**
     * ID가 할당되었는지 확인합니다.
     *
     * @return ID 할당 여부
     */
    public boolean isAssigned() {
        return value != null;
    }
}