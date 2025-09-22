package saviing.game.room.domain.repository;

import java.util.List;

import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.domain.model.vo.RoomId;

/**
 * 배치(Placement) 도메인 객체의 영속성을 담당하는 리포지토리 인터페이스
 * 헥사고날 아키텍처의 포트 역할을 수행
 */
public interface PlacementRepository {

    /**
     * 특정 방의 모든 배치 정보를 조회
     *
     * @param roomId 조회할 방의 식별자
     * @return 해당 방의 배치 목록. 배치가 없으면 빈 리스트 반환
     * @throws IllegalArgumentException roomId가 null인 경우
     */
    List<Placement> findByRoomId(RoomId roomId);

    /**
     * 특정 방의 모든 배치 정보를 삭제
     *
     * @param roomId 삭제할 방의 식별자
     * @throws IllegalArgumentException roomId가 null인 경우
     */
    void deleteByRoomId(RoomId roomId);

    /**
     * 특정 방의 배치 정보를 일괄 저장
     * 기존 배치를 모두 교체하는 방식으로 동작
     *
     * @param roomId 저장할 방의 식별자
     * @param placements 저장할 배치 목록
     * @throws IllegalArgumentException roomId가 null이거나 placements가 null인 경우
     */
    void saveAll(RoomId roomId, List<Placement> placements);
}