package saviing.game.room.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.domain.model.aggregate.Category;
import saviing.game.room.domain.model.vo.*;

import static org.assertj.core.api.Assertions.*;

@DisplayName("배치 도메인 테스트")
class PlacementTests {

    @Test
    @DisplayName("유효한 값으로 배치를 생성할 수 있다")
    void 배치_생성_성공() {
        // Given
        RoomId roomId = new RoomId(100L);
        Long inventoryItemId = 200L;
        Position position = new Position(5, 10);
        ItemSize size = new ItemSize(3, 2);
        Category category = Category.LEFT;

        // When
        Placement placement = Placement.create(roomId, inventoryItemId, position, size, category);

        // Then
        assertThat(placement.getRoomId()).isEqualTo(roomId);
        assertThat(placement.getInventoryItemId()).isEqualTo(inventoryItemId);
        assertThat(placement.getPosition()).isEqualTo(position);
        assertThat(placement.getSize()).isEqualTo(size);
        assertThat(placement.getCategory()).isEqualTo(category);
        assertThat(placement.getCreatedAt()).isNotNull();
        assertThat(placement.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("null 파라미터로 배치 생성 시 예외가 발생한다")
    void 배치_생성_실패_null_파라미터() {
        // Given
        RoomId roomId = new RoomId(100L);
        Long inventoryItemId = 200L;
        Position position = new Position(5, 10);
        ItemSize size = new ItemSize(3, 2);

        // When & Then
        assertThatThrownBy(() -> Placement.create(null, inventoryItemId, position, size, Category.LEFT))
            .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> Placement.create(roomId, null, position, size, Category.LEFT))
            .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> Placement.create(roomId, inventoryItemId, null, size, Category.LEFT))
            .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> Placement.create(roomId, inventoryItemId, position, null, Category.LEFT))
            .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> Placement.create(roomId, inventoryItemId, position, size, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("잘못된 인벤토리 아이템 ID로 배치 생성 시 예외가 발생한다")
    void 배치_생성_실패_잘못된_인벤토리_아이템_ID() {
        // Given
        RoomId roomId = new RoomId(100L);
        Position position = new Position(5, 10);
        ItemSize size = new ItemSize(3, 2);
        Category category = Category.LEFT;

        // When & Then
        assertThatThrownBy(() -> Placement.create(roomId, 0L, position, size, category))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("InventoryItemId must be positive");

        assertThatThrownBy(() -> Placement.create(roomId, -1L, position, size, category))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("InventoryItemId must be positive");
    }

    @Test
    @DisplayName("배치 위치를 변경할 수 있다")
    void 배치_위치_변경() {
        // Given
        Placement placement = createTestPlacement();
        Position newPosition = new Position(10, 15);

        // When
        placement.moveTo(newPosition);

        // Then
        assertThat(placement.getPosition()).isEqualTo(newPosition);
    }

    @Test
    @DisplayName("null 위치로 변경 시 예외가 발생한다")
    void 배치_위치_변경_실패_null() {
        // Given
        Placement placement = createTestPlacement();

        // When & Then
        assertThatThrownBy(() -> placement.moveTo(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("배치 크기를 변경할 수 있다")
    void 배치_크기_변경() {
        // Given
        Placement placement = createTestPlacement();
        ItemSize newSize = new ItemSize(4, 3);

        // When
        placement.resize(newSize);

        // Then
        assertThat(placement.getSize()).isEqualTo(newSize);
    }

    @Test
    @DisplayName("같은 레이어에서 겹치는 배치를 감지할 수 있다")
    void 배치_겹침_감지() {
        // Given
        Placement placement1 = Placement.create(
            new RoomId(100L), 200L,
            new Position(5, 5), new ItemSize(3, 3), Category.LEFT
        );
        Placement placement2 = Placement.create(
            new RoomId(100L), 201L,
            new Position(7, 7), new ItemSize(3, 3), Category.LEFT
        );

        // When & Then
        assertThat(placement1.overlaps(placement2)).isTrue();
        assertThat(placement2.overlaps(placement1)).isTrue();
    }

    @Test
    @DisplayName("다른 레이어의 배치는 겹치지 않는다")
    void 배치_다른_레이어_겹침_없음() {
        // Given
        Placement placement1 = Placement.create(
            new RoomId(100L), 200L,
            new Position(5, 5), new ItemSize(3, 3), Category.LEFT
        );
        Placement placement2 = Placement.create(
            new RoomId(100L), 201L,
            new Position(5, 5), new ItemSize(3, 3), Category.RIGHT
        );

        // When & Then
        assertThat(placement1.overlaps(placement2)).isFalse();
    }

    @Test
    @DisplayName("떨어진 위치의 배치는 겹치지 않는다")
    void 배치_떨어진_위치_겹침_없음() {
        // Given
        Placement placement1 = Placement.create(
            new RoomId(100L), 200L,
            new Position(0, 0), new ItemSize(3, 3), Category.LEFT
        );
        Placement placement2 = Placement.create(
            new RoomId(100L), 201L,
            new Position(5, 5), new ItemSize(3, 3), Category.LEFT
        );

        // When & Then
        assertThat(placement1.overlaps(placement2)).isFalse();
    }

    private Placement createTestPlacement() {
        return Placement.create(
            new RoomId(100L),
            200L,
            new Position(5, 10),
            new ItemSize(3, 2),
            Category.LEFT
        );
    }
}