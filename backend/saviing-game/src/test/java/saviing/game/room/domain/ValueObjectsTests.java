package saviing.game.room.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import saviing.game.room.domain.model.aggregate.Category;
import saviing.game.room.domain.model.vo.*;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Room 도메인 값 객체 테스트")
class ValueObjectsTests {

    @Test
    @DisplayName("RoomId 생성 테스트")
    void RoomId_생성() {
        // Given & When
        RoomId roomId = new RoomId(100L);

        // Then
        assertThat(roomId.value()).isEqualTo(100L);
    }

    @Test
    @DisplayName("RoomId 잘못된 값으로 생성 시 예외 발생")
    void RoomId_잘못된_값_예외() {
        // When & Then
        assertThatThrownBy(() -> new RoomId(null))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new RoomId(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("roomId must be positive");

        assertThatThrownBy(() -> new RoomId(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("roomId must be positive");
    }


    @Test
    @DisplayName("Position 생성 및 거리 계산 테스트")
    void Position_생성_및_거리_계산() {
        // Given
        Position position1 = new Position(0, 0);
        Position position2 = new Position(3, 4);

        // When
        int distance = position1.manhattanDistanceTo(position2);

        // Then
        assertThat(position1.x()).isEqualTo(0);
        assertThat(position1.y()).isEqualTo(0);
        assertThat(distance).isEqualTo(7); // |3-0| + |4-0| = 7
    }

    @Test
    @DisplayName("Position 음수 좌표로 생성 시 예외 발생")
    void Position_음수_좌표_예외() {
        // When & Then
        assertThatThrownBy(() -> new Position(-1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Position coordinates must be non-negative");

        assertThatThrownBy(() -> new Position(0, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Position coordinates must be non-negative");
    }

    @Test
    @DisplayName("ItemSize 생성 및 면적 계산 테스트")
    void ItemSize_생성_및_면적_계산() {
        // Given
        ItemSize size = new ItemSize(3, 4);

        // When
        int area = size.area();
        Position endPosition = size.endPositionFrom(new Position(1, 1));

        // Then
        assertThat(size.xLength()).isEqualTo(3);
        assertThat(size.yLength()).isEqualTo(4);
        assertThat(area).isEqualTo(12);
        assertThat(endPosition.x()).isEqualTo(4); // 1 + 3
        assertThat(endPosition.y()).isEqualTo(5); // 1 + 4
    }

    @Test
    @DisplayName("ItemSize 0 이하 크기로 생성 시 예외 발생")
    void ItemSize_0이하_크기_예외() {
        // When & Then
        assertThatThrownBy(() -> new ItemSize(0, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ItemSize dimensions must be positive");

        assertThatThrownBy(() -> new ItemSize(1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ItemSize dimensions must be positive");

        assertThatThrownBy(() -> new ItemSize(-1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ItemSize dimensions must be positive");
    }

    @Test
    @DisplayName("Category 열거형 값 확인")
    void Category_열거형_값() {
        // When & Then
        assertThat(Category.values()).containsExactly(Category.LEFT, Category.RIGHT, Category.BOTTOM, Category.ROOM_COLOR, Category.PET);
    }
}