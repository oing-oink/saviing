package saviing.game.room.domain.repository;

import java.util.Optional;

import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.domain.model.vo.RoomId;

/**
 * 배치(Placement) 도메인 객체의 영속성을 담당하는 리포지토리 인터페이스
 * 헥사고날 아키텍처의 포트 역할을 수행하며, 룸별로 하나의 Placement 애그리거트를 관리
 */
public interface PlacementRepository {

    /**
     * 특정 방의 배치 정보를 조회
     * 룸별로 하나의 Placement 애그리거트가 존재하며, 해당 애그리거트 내에 모든 배치 아이템들이 포함됨
     *
     * @param roomId 조회할 방의 식별자
     * @return 해당 방의 Placement 애그리거트 (Optional)
     * @throws IllegalArgumentException roomId가 null인 경우
     */
    Optional<Placement> findByRoomId(RoomId roomId);

    /**
     * 특정 방의 배치 정보를 저장
     * 기존 배치를 완전히 교체하는 방식으로 동작 (delete 후 insert)
     *
     * @param placement 저장할 Placement 애그리거트
     * @return 저장된 Placement 애그리거트
     * @throws IllegalArgumentException placement가 null인 경우
     */
    Placement save(Placement placement);

    /**
     * 특정 방의 모든 배치 정보를 삭제
     * 해당 룸의 모든 PlacementEntity 레코드를 삭제
     *
     * @param roomId 삭제할 방의 식별자
     * @throws IllegalArgumentException roomId가 null인 경우
     */
    void deleteByRoomId(RoomId roomId);
}