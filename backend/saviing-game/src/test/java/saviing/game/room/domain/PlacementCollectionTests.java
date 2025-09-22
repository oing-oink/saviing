package saviing.game.room.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import saviing.game.room.domain.model.aggregate.Category;
import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.domain.model.dto.PlacementDraft;
import saviing.game.room.domain.model.vo.RoomId;
import saviing.game.room.domain.service.PlacementCollection;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("배치 컬렉션 도메인 서비스 테스트")
class PlacementCollectionTests {

    @Test
    @DisplayName("유효한 배치 요청으로 배치 목록을 생성할 수 있다")
    void 배치_목록_생성_성공() {
        // Given
        RoomId roomId = new RoomId(100L);
        List<PlacementDraft> drafts = List.of(
            new PlacementDraft(1L, 0, 0, 2, 2, Category.LEFT),
            new PlacementDraft(2L, 5, 5, 1, 1, Category.RIGHT)
        );

        // When
        List<Placement> placements = PlacementCollection.validateAndCreate(roomId, drafts);

        // Then
        assertThat(placements).hasSize(2);
        assertThat(placements.get(0).getInventoryItemId()).isEqualTo(1L);
        assertThat(placements.get(1).getInventoryItemId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("빈 배치 요청으로 빈 목록을 반환한다")
    void 빈_배치_목록() {
        // Given
        RoomId roomId = new RoomId(100L);
        List<PlacementDraft> drafts = List.of();

        // When
        List<Placement> placements = PlacementCollection.validateAndCreate(roomId, drafts);

        // Then
        assertThat(placements).isEmpty();
    }

    @Test
    @DisplayName("중복된 인벤토리 아이템 배치 시 예외가 발생한다")
    void 중복_인벤토리_아이템_예외() {
        // Given
        RoomId roomId = new RoomId(100L);
        List<PlacementDraft> drafts = List.of(
            new PlacementDraft(1L, 0, 0, 2, 2, Category.LEFT),
            new PlacementDraft(1L, 5, 5, 1, 1, Category.RIGHT)
        );

        // When & Then
        assertThatThrownBy(() -> PlacementCollection.validateAndCreate(roomId, drafts))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Duplicate inventory item placement: 1");
    }

    @Test
    @DisplayName("펫 배치 개수 제한을 초과하면 예외가 발생한다")
    void 펫_개수_제한_초과_예외() {
        // Given
        RoomId roomId = new RoomId(100L);
        List<PlacementDraft> drafts = List.of(
            new PlacementDraft(1L, 0, 0, 1, 1, Category.PET),
            new PlacementDraft(2L, 2, 2, 1, 1, Category.PET),
            new PlacementDraft(3L, 4, 4, 1, 1, Category.PET)
        );

        // When & Then
        assertThatThrownBy(() -> PlacementCollection.validateAndCreate(roomId, drafts))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cannot place more than 2 pets. Found: 3");
    }

    @Test
    @DisplayName("같은 레이어에서 겹치는 배치 시 예외가 발생한다")
    void 겹치는_배치_예외() {
        // Given
        RoomId roomId = new RoomId(100L);
        List<PlacementDraft> drafts = List.of(
            new PlacementDraft(1L, 0, 0, 3, 3, Category.LEFT),
            new PlacementDraft(2L, 2, 2, 3, 3, Category.LEFT)
        );

        // When & Then
        assertThatThrownBy(() -> PlacementCollection.validateAndCreate(roomId, drafts))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Placements overlap: item 1 and item 2");
    }

    @Test
    @DisplayName("다른 레이어의 겹치는 위치는 허용된다")
    void 다른_레이어_겹침_허용() {
        // Given
        RoomId roomId = new RoomId(100L);
        List<PlacementDraft> drafts = List.of(
            new PlacementDraft(1L, 0, 0, 3, 3, Category.LEFT),
            new PlacementDraft(2L, 0, 0, 3, 3, Category.RIGHT)
        );

        // When & Then
        assertThatCode(() -> PlacementCollection.validateAndCreate(roomId, drafts))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("PlacementDraft 생성 시 잘못된 값으로 예외가 발생한다")
    void PlacementDraft_생성_실패() {
        // When & Then
        assertThatThrownBy(() -> new PlacementDraft(null, 0, 0, 1, 1, Category.LEFT))
            .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new PlacementDraft(0L, 0, 0, 1, 1, Category.LEFT))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("InventoryItemId must be positive");

        assertThatThrownBy(() -> new PlacementDraft(1L, -1, 0, 1, 1, Category.LEFT))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Position coordinates must be non-negative");

        assertThatThrownBy(() -> new PlacementDraft(1L, 0, -1, 1, 1, Category.LEFT))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Position coordinates must be non-negative");

        assertThatThrownBy(() -> new PlacementDraft(1L, 0, 0, 0, 1, Category.LEFT))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ItemSize dimensions must be positive");

        assertThatThrownBy(() -> new PlacementDraft(1L, 0, 0, 1, 0, Category.LEFT))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ItemSize dimensions must be positive");

        assertThatThrownBy(() -> new PlacementDraft(1L, 0, 0, 1, 1, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("펫 2마리까지는 배치 가능하다")
    void 펫_2마리_배치_허용() {
        // Given
        RoomId roomId = new RoomId(100L);
        List<PlacementDraft> drafts = List.of(
            new PlacementDraft(1L, 0, 0, 1, 1, Category.PET),
            new PlacementDraft(2L, 2, 2, 1, 1, Category.PET),
            new PlacementDraft(3L, 4, 4, 1, 1, Category.LEFT)
        );

        // When & Then
        assertThatCode(() -> PlacementCollection.validateAndCreate(roomId, drafts))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("null roomId로 배치 검증 시 예외가 발생한다")
    void null_roomId_예외() {
        // Given
        List<PlacementDraft> drafts = List.of(
            new PlacementDraft(1L, 0, 0, 1, 1, Category.LEFT)
        );

        // When & Then
        assertThatThrownBy(() -> PlacementCollection.validateAndCreate(null, drafts))
            .isInstanceOf(NullPointerException.class);
    }
}
