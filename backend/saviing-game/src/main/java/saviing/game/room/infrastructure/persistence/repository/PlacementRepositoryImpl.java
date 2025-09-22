package saviing.game.room.infrastructure.persistence.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.domain.model.vo.RoomId;
import saviing.game.room.domain.repository.PlacementRepository;
import saviing.game.room.infrastructure.persistence.entity.PlacementEntity;
import saviing.game.room.infrastructure.persistence.mapper.PlacementEntityMapper;

/**
 * PlacementRepository를 구현한 영속성 어댑터
 * 룸별로 하나의 Placement 애그리거트를 관리하며,
 * 내부적으로는 여러 PlacementEntity로 저장하여 단일 테이블 구조를 유지
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlacementRepositoryImpl implements PlacementRepository {

    private final PlacementJpaRepository placementJpaRepository;

    /**
     * 방 식별자로 배치 애그리거트를 조회
     * 해당 룸의 모든 PlacementEntity를 조회하여 하나의 Placement 애그리거트로 변환
     *
     * @param roomId 방 식별자 값 객체
     * @return 조회된 Placement 애그리거트 (Optional)
     * @throws IllegalArgumentException roomId가 null인 경우
     */
    @Override
    public Optional<Placement> findByRoomId(RoomId roomId) {
        Objects.requireNonNull(roomId, "roomId");

        List<PlacementEntity> entities = placementJpaRepository.findByRoomIdOrderByPlacementIdAsc(roomId.value());
        return PlacementEntityMapper.toDomain(entities);
    }

    /**
     * Placement 애그리거트를 저장
     * 기존 배치를 완전히 교체하는 방식으로 동작 (delete 후 insert)
     * 트랜잭션 내에서 원자적으로 처리됨
     *
     * @param placement 저장할 Placement 애그리거트
     * @return 저장된 Placement 애그리거트
     * @throws IllegalArgumentException placement가 null인 경우
     */
    @Override
    @Transactional
    public Placement save(Placement placement) {
        Objects.requireNonNull(placement, "placement");

        RoomId roomId = placement.getRoomId();

        // 1. 기존 배치 삭제 (교체 방식)
        placementJpaRepository.deleteByRoomId(roomId.value());

        // 2. 새로운 배치 저장
        if (!placement.getPlacedItems().isEmpty()) {
            List<PlacementEntity> entities = PlacementEntityMapper.toEntities(placement);
            placementJpaRepository.saveAll(entities);
        }

        // 3. 저장된 결과 반환 (ID가 할당된 상태로)
        return findByRoomId(roomId)
            .orElse(Placement.create(roomId));
    }

    /**
     * 방 식별자로 모든 배치를 삭제
     * 해당 룸의 모든 PlacementEntity 레코드를 삭제
     *
     * @param roomId 방 식별자 값 객체
     * @throws IllegalArgumentException roomId가 null인 경우
     */
    @Override
    @Transactional
    public void deleteByRoomId(RoomId roomId) {
        Objects.requireNonNull(roomId, "roomId");
        placementJpaRepository.deleteByRoomId(roomId.value());
    }
}