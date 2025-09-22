package saviing.game.room.infrastructure.persistence.repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.domain.model.vo.RoomId;
import saviing.game.room.domain.repository.PlacementRepository;
import saviing.game.room.infrastructure.persistence.entity.PlacementEntity;
import saviing.game.room.infrastructure.persistence.mapper.PlacementEntityMapper;

/**
 * PlacementRepository를 구현한 영속성 어댑터입니다.
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlacementRepositoryImpl implements PlacementRepository {

    private final PlacementJpaRepository placementJpaRepository;
    private final PlacementEntityMapper placementEntityMapper;

    /**
     * 방 식별자로 배치 목록을 조회합니다.
     *
     * @param roomId 방 식별자 값 객체
     * @return 조회된 배치 도메인 목록
     */
    @Override
    public List<Placement> findByRoomId(RoomId roomId) {
        Objects.requireNonNull(roomId, "roomId");

        List<PlacementEntity> entities = placementJpaRepository.findByRoomIdOrderByPlacementIdAsc(roomId.value());
        return entities.stream()
            .map(placementEntityMapper::toDomain)
            .collect(Collectors.toList());
    }

    /**
     * 방 식별자로 배치를 삭제합니다.
     *
     * @param roomId 방 식별자 값 객체
     */
    @Override
    @Transactional
    public void deleteByRoomId(RoomId roomId) {
        Objects.requireNonNull(roomId, "roomId");
        placementJpaRepository.deleteByRoomId(roomId.value());
    }

    /**
     * 특정 방의 배치 목록을 저장합니다.
     *
     * @param roomId 방 식별자 값 객체
     * @param placements 저장할 배치 도메인 목록
     */
    @Override
    @Transactional
    public void saveAll(RoomId roomId, List<Placement> placements) {
        Objects.requireNonNull(roomId, "roomId");
        Objects.requireNonNull(placements, "placements");

        for (Placement placement : placements) {
            if (!placement.getRoomId().equals(roomId)) {
                throw new IllegalArgumentException("모든 배치는 동일한 roomId를 가져야 합니다");
            }
        }

        List<PlacementEntity> entities = placements.stream()
            .map(placementEntityMapper::toEntity)
            .collect(Collectors.toList());

        placementJpaRepository.saveAll(entities);
    }
}
