package saviing.game.room.domain.model.aggregate;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Getter;
import lombok.NonNull;
import saviing.game.room.domain.model.vo.ItemSize;
import saviing.game.room.domain.model.vo.Position;

/**
 * 방 내 배치된 개별 아이템을 나타내는 도메인 엔티티
 * 인벤토리 아이템이 방의 특정 위치에 배치된 상태 정보를 담고 있음
 */
@Getter
public class PlacedItem {

    private final Long inventoryItemId;
    private final Position position;
    private final ItemSize size;
    private final Category category;
    private final LocalDateTime createdAt;

    /**
     * PlacedItem 생성자 (내부용)
     *
     * @param inventoryItemId 인벤토리 아이템 식별자
     * @param position 배치 위치
     * @param size 아이템 크기
     * @param category 배치 카테고리
     * @param createdAt 생성 시각
     */
    private PlacedItem(Long inventoryItemId, Position position, ItemSize size,
            Category category, LocalDateTime createdAt) {
        this.inventoryItemId = inventoryItemId;
        this.position = position;
        this.size = size;
        this.category = category;
        this.createdAt = createdAt;
    }

    /**
     * 새로운 배치 아이템을 생성하는 정적 팩토리 메서드
     *
     * @param inventoryItemId 인벤토리 아이템 식별자
     * @param position 배치 위치
     * @param size 아이템 크기
     * @param category 배치 카테고리
     * @return 새로 생성된 PlacedItem 인스턴스
     * @throws IllegalArgumentException 필수 파라미터가 null이거나 잘못된 값인 경우
     */
    public static PlacedItem create(
        @NonNull Long inventoryItemId,
        @NonNull Position position,
        @NonNull ItemSize size,
        @NonNull Category category
    ) {

        if (inventoryItemId <= 0) {
            throw new IllegalArgumentException("InventoryItemId must be positive");
        }

        LocalDateTime now = LocalDateTime.now();
        return new PlacedItem(inventoryItemId, position, size, category, now);
    }

    /**
     * 기존 배치 아이템을 복원하는 정적 팩토리 메서드 (영속성 계층용)
     *
     * @param inventoryItemId 인벤토리 아이템 식별자
     * @param position 배치 위치
     * @param size 아이템 크기
     * @param category 배치 카테고리
     * @param createdAt 생성 시각
     * @return 복원된 PlacedItem 인스턴스
     * @throws IllegalArgumentException 필수 파라미터가 null인 경우
     */
    public static PlacedItem restore(
        @NonNull Long inventoryItemId,
        @NonNull Position position,
        @NonNull ItemSize size,
        @NonNull Category category,
        @NonNull LocalDateTime createdAt
    ) {
        return new PlacedItem(inventoryItemId, position, size, category, createdAt);
    }

    /**
     * 다른 배치 아이템과 위치가 겹치는지 확인
     * 같은 카테고리의 아이템끼리만 겹침을 검사함
     *
     * @param other 비교할 다른 배치 아이템
     * @return 겹치면 true, 그렇지 않으면 false
     */
    public boolean overlaps(PlacedItem other) {
        if (other == null || !this.category.equals(other.category)) {
            return false;
        }

        final Position thisEnd = this.size.endPositionFrom(this.position);
        final Position otherEnd = other.size.endPositionFrom(other.position);

        return !(this.position.x() >= otherEnd.x() ||
                other.position.x() >= thisEnd.x() ||
                this.position.y() >= otherEnd.y() ||
                other.position.y() >= thisEnd.y());
    }

    /**
     * 펫 아이템인지 확인
     *
     * @return 펫 카테고리이면 true, 그렇지 않으면 false
     */
    public boolean isPet() {
        return category == Category.PET;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlacedItem that = (PlacedItem) o;
        return Objects.equals(inventoryItemId, that.inventoryItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryItemId);
    }
}