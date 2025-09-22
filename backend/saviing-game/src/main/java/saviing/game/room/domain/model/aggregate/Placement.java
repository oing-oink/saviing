package saviing.game.room.domain.model.aggregate;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

import saviing.game.room.domain.model.vo.ItemSize;
import saviing.game.room.domain.model.vo.PlacementId;
import saviing.game.room.domain.model.vo.Position;
import saviing.game.room.domain.model.vo.RoomId;

/**
 * 방 내 아이템 배치를 나타내는 애그리거트 루트
 * 인벤토리 아이템이 방의 특정 위치에 배치된 상태를 관리
 */
@Getter
public class Placement {

    private final PlacementId placementId;
    private final RoomId roomId;
    private final Long inventoryItemId;
    private Position position;
    private ItemSize size;
    private Category category;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Placement 생성자 (내부용)
     *
     * @param placementId 배치 식별자
     * @param roomId 방 식별자
     * @param inventoryItemId 인벤토리 아이템 식별자
     * @param position 배치 위치
     * @param size 아이템 크기
     * @param category 배치 카테고리
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     */
    private Placement(PlacementId placementId, RoomId roomId, Long inventoryItemId,
        Position position, ItemSize size, Category category,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.placementId = placementId;
        this.roomId = roomId;
        this.inventoryItemId = inventoryItemId;
        this.position = position;
        this.size = size;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 새로운 배치를 생성하는 정적 팩토리 메서드
     *
     * @param roomId 방 식별자
     * @param inventoryItemId 인벤토리 아이템 식별자
     * @param position 배치 위치
     * @param size 아이템 크기
     * @param category 배치 카테고리
     * @return 새로 생성된 Placement 인스턴스 (식별자는 아직 할당되지 않을 수 있음)
     * @throws IllegalArgumentException 필수 파라미터가 null인 경우
     */
    public static Placement create(RoomId roomId, Long inventoryItemId,
        Position position, ItemSize size, Category category) {

        validateCreateParameters(roomId, inventoryItemId, position, size, category);

        LocalDateTime now = LocalDateTime.now();
        return new Placement(null, roomId, inventoryItemId, position, size, category, now, now);
    }

    /**
     * 기존 배치를 복원하는 정적 팩토리 메서드 (영속성 계층용)
     *
     * @param placementId 배치 식별자
     * @param roomId 방 식별자
     * @param inventoryItemId 인벤토리 아이템 식별자
     * @param position 배치 위치
     * @param size 아이템 크기
     * @param category 배치 카테고리
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 Placement 인스턴스
     */
    public static Placement restore(PlacementId placementId, RoomId roomId, Long inventoryItemId,
        Position position, ItemSize size, Category category,
        LocalDateTime createdAt, LocalDateTime updatedAt) {

        Objects.requireNonNull(placementId, "placementId");
        return new Placement(placementId, roomId, inventoryItemId, position, size, category, createdAt, updatedAt);
    }

    /**
     * 배치 위치를 변경
     *
     * @param newPosition 새로운 위치
     * @throws IllegalArgumentException newPosition이 null인 경우
     */
    public void moveTo(Position newPosition) {
        Objects.requireNonNull(newPosition, "newPosition");
        this.position = newPosition;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 아이템 크기를 변경
     *
     * @param newSize 새로운 크기
     * @throws IllegalArgumentException newSize가 null인 경우
     */
    public void resize(ItemSize newSize) {
        Objects.requireNonNull(newSize, "newSize");
        this.size = newSize;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 배치 카테고리를 변경
     *
     * @param newCategory 새로운 카테고리
     * @throws IllegalArgumentException newCategory가 null인 경우
     */
    public void changeCategory(Category newCategory) {
        Objects.requireNonNull(newCategory, "newCategory");
        this.category = newCategory;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 다른 배치와 위치가 겹치는지 확인
     *
     * @param other 비교할 다른 배치
     * @return 겹치면 true, 그렇지 않으면 false
     */
    public boolean overlaps(Placement other) {
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

    private static void validateCreateParameters(RoomId roomId, Long inventoryItemId,
        Position position, ItemSize size, Category category) {

        Objects.requireNonNull(roomId, "roomId");
        Objects.requireNonNull(inventoryItemId, "inventoryItemId");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(size, "size");
        Objects.requireNonNull(category, "category");

        if (inventoryItemId <= 0) {
            throw new IllegalArgumentException("InventoryItemId must be positive");
        }
    }
}
