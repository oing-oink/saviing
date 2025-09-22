package saviing.game.room.infrastructure.persistence;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import saviing.game.room.domain.model.aggregate.Category;
import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.domain.model.vo.ItemSize;
import saviing.game.room.domain.model.vo.Position;
import saviing.game.room.domain.model.vo.RoomId;
import saviing.game.room.domain.repository.PlacementRepository;
import saviing.game.room.infrastructure.persistence.mapper.PlacementEntityMapper;
import saviing.game.room.infrastructure.persistence.repository.PlacementJpaRepository;
import saviing.game.room.infrastructure.persistence.repository.PlacementRepositoryImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@Import({PlacementRepositoryImpl.class, PlacementEntityMapper.class})
@DisplayName("PlacementRepositoryImpl 통합 테스트")
class PlacementRepositoryImplTests {

    @Autowired
    private PlacementRepository placementRepository;

    @Autowired
    private PlacementJpaRepository placementJpaRepository;

    @Test
    @DisplayName("방 식별자로 배치 목록을 조회할 수 있다")
    void 방_식별자로_배치_목록_조회() {
        // given
        RoomId roomId = new RoomId(1L);
        List<Placement> placements = Arrays.asList(
            Placement.create(
                roomId,
                100L,
                new Position(5, 10),
                new ItemSize(2, 3),
                Category.LEFT
            ),
            Placement.create(
                roomId,
                200L,
                new Position(15, 20),
                new ItemSize(4, 5),
                Category.RIGHT
            )
        );

        // when
        placementRepository.saveAll(roomId, placements);
        List<Placement> result = placementRepository.findByRoomId(roomId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
            .extracting(Placement::getInventoryItemId)
            .containsExactly(100L, 200L);
    }

    @Test
    @DisplayName("다른 방의 배치는 조회되지 않는다")
    void 다른_방의_배치는_조회되지_않음() {
        // given
        RoomId roomId1 = new RoomId(1L);
        RoomId roomId2 = new RoomId(2L);

        List<Placement> room1Placements = Arrays.asList(
            Placement.create(roomId1, 100L, new Position(5, 10), new ItemSize(2, 3), Category.LEFT)
        );
        List<Placement> room2Placements = Arrays.asList(
            Placement.create(roomId2, 200L, new Position(15, 20), new ItemSize(4, 5), Category.RIGHT)
        );

        // when
        placementRepository.saveAll(roomId1, room1Placements);
        placementRepository.saveAll(roomId2, room2Placements);

        List<Placement> room1Result = placementRepository.findByRoomId(roomId1);
        List<Placement> room2Result = placementRepository.findByRoomId(roomId2);

        // then
        assertThat(room1Result).hasSize(1);
        assertThat(room1Result.get(0).getInventoryItemId()).isEqualTo(100L);

        assertThat(room2Result).hasSize(1);
        assertThat(room2Result.get(0).getInventoryItemId()).isEqualTo(200L);
    }

    @Test
    @DisplayName("방 식별자로 배치를 삭제할 수 있다")
    void 방_식별자로_배치_삭제() {
        // given
        RoomId roomId = new RoomId(1L);
        List<Placement> placements = Arrays.asList(
            Placement.create(roomId, 100L, new Position(5, 10), new ItemSize(2, 3), Category.LEFT),
            Placement.create(roomId, 200L, new Position(15, 20), new ItemSize(4, 5), Category.RIGHT)
        );
        placementRepository.saveAll(roomId, placements);

        // when
        placementRepository.deleteByRoomId(roomId);
        List<Placement> result = placementRepository.findByRoomId(roomId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("다른 방의 배치 삭제 시 해당 방의 배치만 삭제된다")
    void 다른_방의_배치_삭제시_해당_방만_삭제() {
        // given
        RoomId roomId1 = new RoomId(1L);
        RoomId roomId2 = new RoomId(2L);

        List<Placement> room1Placements = Arrays.asList(
            Placement.create(roomId1, 100L, new Position(5, 10), new ItemSize(2, 3), Category.LEFT)
        );
        List<Placement> room2Placements = Arrays.asList(
            Placement.create(roomId2, 200L, new Position(15, 20), new ItemSize(4, 5), Category.RIGHT)
        );

        placementRepository.saveAll(roomId1, room1Placements);
        placementRepository.saveAll(roomId2, room2Placements);

        // when
        placementRepository.deleteByRoomId(roomId1);

        List<Placement> room1Result = placementRepository.findByRoomId(roomId1);
        List<Placement> room2Result = placementRepository.findByRoomId(roomId2);

        // then
        assertThat(room1Result).isEmpty();
        assertThat(room2Result).hasSize(1);
    }

    @Test
    @DisplayName("배치 저장 시 모든 배치가 동일한 roomId를 가져야 한다")
    void 배치_저장시_동일한_roomId_검증() {
        // given
        RoomId roomId1 = new RoomId(1L);
        RoomId roomId2 = new RoomId(2L);

        List<Placement> mixedPlacements = Arrays.asList(
            Placement.create(roomId1, 100L, new Position(5, 10), new ItemSize(2, 3), Category.LEFT),
            Placement.create(roomId2, 200L, new Position(15, 20), new ItemSize(4, 5), Category.RIGHT)
        );

        // when & then
        assertThatThrownBy(() -> placementRepository.saveAll(roomId1, mixedPlacements))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("모든 배치는 동일한 roomId를 가져야 합니다");
    }

    @Test
    @DisplayName("배치를 저장하고 조회할 때 모든 필드가 올바르게 매핑된다")
    void 배치_필드_매핑_검증() {
        // given
        RoomId roomId = new RoomId(1L);
        Placement placement = Placement.create(
            roomId,
            100L,
            new Position(5, 10),
            new ItemSize(2, 3),
            Category.PET
        );

        // when
        placementRepository.saveAll(roomId, Arrays.asList(placement));
        List<Placement> result = placementRepository.findByRoomId(roomId);

        // then
        assertThat(result).hasSize(1);
        Placement savedPlacement = result.get(0);

        assertThat(savedPlacement.getRoomId()).isEqualTo(roomId);
        assertThat(savedPlacement.getInventoryItemId()).isEqualTo(100L);
        assertThat(savedPlacement.getPosition().x()).isEqualTo(5);
        assertThat(savedPlacement.getPosition().y()).isEqualTo(10);
        assertThat(savedPlacement.getSize().xLength()).isEqualTo(2);
        assertThat(savedPlacement.getSize().yLength()).isEqualTo(3);
        assertThat(savedPlacement.getCategory()).isEqualTo(Category.PET);
        assertThat(savedPlacement.getPlacementId()).isNotNull();
        assertThat(savedPlacement.getCreatedAt()).isNotNull();
        assertThat(savedPlacement.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("빈 배치 목록을 저장할 수 있다")
    void 빈_배치_목록_저장() {
        // given
        RoomId roomId = new RoomId(1L);

        // when & then
        placementRepository.saveAll(roomId, Arrays.asList());
        List<Placement> result = placementRepository.findByRoomId(roomId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 방 ID로 조회시 빈 목록을 반환한다")
    void 존재하지_않는_방_조회시_빈_목록_반환() {
        // given
        RoomId nonExistentRoomId = new RoomId(999L);

        // when
        List<Placement> result = placementRepository.findByRoomId(nonExistentRoomId);

        // then
        assertThat(result).isEmpty();
    }
}