package saviing.game.room.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import saviing.game.room.domain.model.aggregate.Category;
import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.domain.model.aggregate.PlacedItem;
import saviing.game.room.domain.model.vo.ItemSize;
import saviing.game.room.domain.model.vo.Position;
import saviing.game.room.domain.model.vo.RoomId;

/**
 * Placement 애그리거트의 핵심 기능을 검증하는 테스트
 * 배치 규칙 검증, 아이템 관리 등의 도메인 로직을 테스트
 */
@DisplayName("Placement 애그리거트 테스트")
class PlacementAggregateTests {

    @Test
    @DisplayName("새로운 룸 배치를 생성할 수 있다")
    void 새로운_룸_배치_생성() {
        // given
        RoomId roomId = new RoomId(1L);

        // when
        Placement placement = Placement.create(roomId);

        // then
        assertThat(placement.getRoomId()).isEqualTo(roomId);
        assertThat(placement.getPlacedItems()).isEmpty();
        assertThat(placement.getItemCount()).isZero();
    }

    @Test
    @DisplayName("아이템을 배치에 추가할 수 있다")
    void 아이템_배치_추가() {
        // given
        Placement placement = Placement.create(new RoomId(1L));
        PlacedItem item = PlacedItem.create(
            1L,
            100L,
            new Position(10, 20),
            new ItemSize(5, 3),
            Category.LEFT
        );

        // when
        placement.addItem(item);

        // then
        assertThat(placement.getItemCount()).isEqualTo(1);
        assertThat(placement.hasItem(1L)).isTrue();
        assertThat(placement.getPlacedItems()).containsExactly(item);
    }

    @Test
    @DisplayName("중복된 인벤토리 아이템은 배치할 수 없다")
    void 중복_아이템_배치_실패() {
        // given
        Placement placement = Placement.create(new RoomId(1L));
        PlacedItem item1 = PlacedItem.create(1L, 100L, new Position(10, 20), new ItemSize(5, 3), Category.LEFT);
        PlacedItem item2 = PlacedItem.create(1L, 101L, new Position(30, 40), new ItemSize(3, 2), Category.RIGHT);

        placement.addItem(item1);

        // when & then
        assertThatThrownBy(() -> placement.addItem(item2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Duplicate inventory item placement: 1");
    }

    @Test
    @DisplayName("겹치는 위치에는 같은 카테고리 아이템을 배치할 수 없다")
    void 겹치는_위치_배치_실패() {
        // given
        Placement placement = Placement.create(new RoomId(1L));
        PlacedItem item1 = PlacedItem.create(1L, 100L, new Position(10, 10), new ItemSize(5, 5), Category.LEFT);
        PlacedItem item2 = PlacedItem.create(2L, 101L, new Position(12, 12), new ItemSize(5, 5), Category.LEFT);

        placement.addItem(item1);

        // when & then
        assertThatThrownBy(() -> placement.addItem(item2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Placements overlap: item 1 and item 2");
    }

    @Test
    @DisplayName("다른 카테고리 아이템은 같은 위치에 배치할 수 있다")
    void 다른_카테고리_같은_위치_배치_성공() {
        // given
        Placement placement = Placement.create(new RoomId(1L));
        PlacedItem leftItem = PlacedItem.create(1L, 100L, new Position(10, 10), new ItemSize(5, 5), Category.LEFT);
        PlacedItem rightItem = PlacedItem.create(2L, 101L, new Position(10, 10), new ItemSize(5, 5), Category.RIGHT);

        // when
        placement.addItem(leftItem);
        placement.addItem(rightItem);

        // then
        assertThat(placement.getItemCount()).isEqualTo(2);
        assertThat(placement.hasItem(1L)).isTrue();
        assertThat(placement.hasItem(2L)).isTrue();
    }

    @Test
    @DisplayName("펫은 최대 2마리까지만 배치할 수 있다")
    void 펫_개수_제한_검증() {
        // given
        Placement placement = Placement.create(new RoomId(1L));
        PlacedItem pet1 = PlacedItem.create(1L, 100L, new Position(10, 10), new ItemSize(3, 3), Category.PET);
        PlacedItem pet2 = PlacedItem.create(2L, 101L, new Position(20, 20), new ItemSize(3, 3), Category.PET);
        PlacedItem pet3 = PlacedItem.create(3L, 102L, new Position(30, 30), new ItemSize(3, 3), Category.PET);

        placement.addItem(pet1);
        placement.addItem(pet2);

        // when & then
        assertThatThrownBy(() -> placement.addItem(pet3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Cannot place more than 2 pets. Found: 3");
    }

    @Test
    @DisplayName("인벤토리 아이템 ID로 배치에서 제거할 수 있다")
    void 아이템_제거() {
        // given
        Placement placement = Placement.create(new RoomId(1L));
        PlacedItem item = PlacedItem.create(1L, 100L, new Position(10, 20), new ItemSize(5, 3), Category.LEFT);
        placement.addItem(item);

        // when
        boolean removed = placement.removeItem(1L);

        // then
        assertThat(removed).isTrue();
        assertThat(placement.getItemCount()).isZero();
        assertThat(placement.hasItem(1L)).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 아이템 제거는 false를 반환한다")
    void 존재하지_않는_아이템_제거() {
        // given
        Placement placement = Placement.create(new RoomId(1L));

        // when
        boolean removed = placement.removeItem(999L);

        // then
        assertThat(removed).isFalse();
    }

    @Test
    @DisplayName("모든 배치 아이템을 새로운 목록으로 교체할 수 있다")
    void 모든_아이템_교체() {
        // given
        Placement placement = Placement.create(new RoomId(1L));
        PlacedItem oldItem = PlacedItem.create(1L, 100L, new Position(10, 10), new ItemSize(3, 3), Category.LEFT);
        placement.addItem(oldItem);

        List<PlacedItem> newItems = List.of(
            PlacedItem.create(2L, 101L, new Position(20, 20), new ItemSize(4, 4), Category.RIGHT),
            PlacedItem.create(3L, 102L, new Position(30, 30), new ItemSize(2, 2), Category.BOTTOM)
        );

        // when
        placement.replaceAllItems(newItems);

        // then
        assertThat(placement.getItemCount()).isEqualTo(2);
        assertThat(placement.hasItem(1L)).isFalse();
        assertThat(placement.hasItem(2L)).isTrue();
        assertThat(placement.hasItem(3L)).isTrue();
    }

    @Test
    @DisplayName("교체할 아이템 목록에서도 배치 규칙이 검증된다")
    void 교체_아이템_규칙_검증() {
        // given
        Placement placement = Placement.create(new RoomId(1L));

        List<PlacedItem> invalidItems = List.of(
            PlacedItem.create(1L, 100L, new Position(10, 10), new ItemSize(5, 5), Category.LEFT),
            PlacedItem.create(2L, 101L, new Position(12, 12), new ItemSize(5, 5), Category.LEFT) // 겹침
        );

        // when & then
        assertThatThrownBy(() -> placement.replaceAllItems(invalidItems))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Placements overlap");
    }

    @Test
    @DisplayName("빈 목록으로 교체하면 모든 아이템이 제거된다")
    void 빈_목록으로_교체() {
        // given
        Placement placement = Placement.create(new RoomId(1L));
        PlacedItem item = PlacedItem.create(1L, 100L, new Position(10, 10), new ItemSize(3, 3), Category.LEFT);
        placement.addItem(item);

        // when
        placement.replaceAllItems(List.of());

        // then
        assertThat(placement.getItemCount()).isZero();
        assertThat(placement.getPlacedItems()).isEmpty();
    }
}