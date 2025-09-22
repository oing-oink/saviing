package saviing.game.room.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import saviing.game.room.domain.model.aggregate.Category;
import saviing.game.room.domain.model.aggregate.PlacedItem;
import saviing.game.room.domain.model.vo.ItemSize;
import saviing.game.room.domain.model.vo.Position;

/**
 * PlacedItem 엔티티의 핵심 기능을 검증하는 테스트
 * 겹침 검사, 펫 여부 확인 등의 도메인 로직을 테스트
 */
@DisplayName("PlacedItem 엔티티 테스트")
class PlacedItemTests {

    @Test
    @DisplayName("유효한 파라미터로 PlacedItem을 생성할 수 있다")
    void PlacedItem_생성_성공() {
        // given
        Long inventoryItemId = 1L;
        Position position = new Position(10, 20);
        ItemSize size = new ItemSize(5, 3);
        Category category = Category.LEFT;

        // when
        PlacedItem item = PlacedItem.create(inventoryItemId, position, size, category);

        // then
        assertThat(item.getInventoryItemId()).isEqualTo(inventoryItemId);
        assertThat(item.getPosition()).isEqualTo(position);
        assertThat(item.getSize()).isEqualTo(size);
        assertThat(item.getCategory()).isEqualTo(category);
        assertThat(item.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("inventoryItemId가 null이면 생성에 실패한다")
    void inventoryItemId_null_생성_실패() {
        // when & then
        assertThatThrownBy(() -> PlacedItem.create(
            null,
            new Position(10, 20),
            new ItemSize(5, 3),
            Category.LEFT
        ))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("inventoryItemId");
    }

    @Test
    @DisplayName("inventoryItemId가 0 이하이면 생성에 실패한다")
    void inventoryItemId_음수_생성_실패() {
        // when & then
        assertThatThrownBy(() -> PlacedItem.create(
            0L,
            new Position(10, 20),
            new ItemSize(5, 3),
            Category.LEFT
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("InventoryItemId must be positive");
    }

    @Test
    @DisplayName("같은 카테고리의 아이템끼리 겹침을 올바르게 판단한다")
    void 같은_카테고리_겹침_검사() {
        // given
        PlacedItem item1 = PlacedItem.create(1L, new Position(10, 10), new ItemSize(5, 5), Category.LEFT);
        PlacedItem item2 = PlacedItem.create(2L, new Position(12, 12), new ItemSize(5, 5), Category.LEFT);

        // when
        boolean overlaps = item1.overlaps(item2);

        // then
        assertThat(overlaps).isTrue();
    }

    @Test
    @DisplayName("다른 카테고리의 아이템끼리는 겹치지 않는다고 판단한다")
    void 다른_카테고리_겹침_검사() {
        // given
        PlacedItem leftItem = PlacedItem.create(1L, new Position(10, 10), new ItemSize(5, 5), Category.LEFT);
        PlacedItem rightItem = PlacedItem.create(2L, new Position(10, 10), new ItemSize(5, 5), Category.RIGHT);

        // when
        boolean overlaps = leftItem.overlaps(rightItem);

        // then
        assertThat(overlaps).isFalse();
    }

    @Test
    @DisplayName("같은 카테고리이지만 떨어진 위치의 아이템은 겹치지 않는다")
    void 같은_카테고리_떨어진_위치_겹침_검사() {
        // given
        PlacedItem item1 = PlacedItem.create(1L, new Position(10, 10), new ItemSize(5, 5), Category.LEFT);
        PlacedItem item2 = PlacedItem.create(2L, new Position(20, 20), new ItemSize(5, 5), Category.LEFT);

        // when
        boolean overlaps = item1.overlaps(item2);

        // then
        assertThat(overlaps).isFalse();
    }

    @Test
    @DisplayName("경계선에 맞닿은 아이템은 겹치지 않는다고 판단한다")
    void 경계선_맞닿음_겹침_검사() {
        // given - item1은 (10,10)에서 (15,15)까지, item2는 (15,15)에서 (20,20)까지
        PlacedItem item1 = PlacedItem.create(1L, new Position(10, 10), new ItemSize(5, 5), Category.LEFT);
        PlacedItem item2 = PlacedItem.create(2L, new Position(15, 15), new ItemSize(5, 5), Category.LEFT);

        // when
        boolean overlaps = item1.overlaps(item2);

        // then
        assertThat(overlaps).isFalse();
    }

    @Test
    @DisplayName("PET 카테고리의 아이템은 펫으로 인식된다")
    void 펫_카테고리_확인() {
        // given
        PlacedItem petItem = PlacedItem.create(1L, new Position(10, 10), new ItemSize(3, 3), Category.PET);
        PlacedItem normalItem = PlacedItem.create(2L, new Position(20, 20), new ItemSize(5, 5), Category.LEFT);

        // when & then
        assertThat(petItem.isPet()).isTrue();
        assertThat(normalItem.isPet()).isFalse();
    }

    @Test
    @DisplayName("같은 inventoryItemId를 가진 아이템은 동등하다")
    void 동등성_검사() {
        // given
        PlacedItem item1 = PlacedItem.create(1L, new Position(10, 10), new ItemSize(5, 5), Category.LEFT);
        PlacedItem item2 = PlacedItem.create(1L, new Position(20, 20), new ItemSize(3, 3), Category.RIGHT);

        // when & then
        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    @DisplayName("다른 inventoryItemId를 가진 아이템은 동등하지 않다")
    void 동등성_검사_실패() {
        // given
        PlacedItem item1 = PlacedItem.create(1L, new Position(10, 10), new ItemSize(5, 5), Category.LEFT);
        PlacedItem item2 = PlacedItem.create(2L, new Position(10, 10), new ItemSize(5, 5), Category.LEFT);

        // when & then
        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    @DisplayName("null과 비교하면 동등하지 않다")
    void null_동등성_검사() {
        // given
        PlacedItem item = PlacedItem.create(1L, new Position(10, 10), new ItemSize(5, 5), Category.LEFT);

        // when & then
        assertThat(item.overlaps(null)).isFalse();
        assertThat(item).isNotEqualTo(null);
    }
}