package saviing.game.room.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import saviing.game.room.infrastructure.persistence.entity.PlacementEntity;

/**
 * room_placement 테이블에 접근하기 위한 JPA 리포지토리입니다.
 */
public interface PlacementJpaRepository extends JpaRepository<PlacementEntity, Long> {

    /**
     * 방 식별자로 배치를 조회합니다.
     *
     * @param roomId 방 식별자
     * @return 해당 방에 저장된 배치 엔티티 목록
     */
    List<PlacementEntity> findByRoomIdOrderByPlacementIdAsc(Long roomId);

    /**
     * 방 식별자로 배치를 삭제합니다.
     *
     * @param roomId 방 식별자
     */
    void deleteByRoomId(Long roomId);
}
