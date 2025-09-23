package saviing.game.room.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import saviing.game.room.infrastructure.persistence.entity.RoomEntity;

/**
 * Room 엔티티에 대한 JPA Repository 인터페이스
 * Spring Data JPA를 활용하여 데이터베이스 접근 기능을 제공한다.
 * 캐릭터별 방 관리를 위한 특화된 쿼리 메서드들을 포함한다.
 */
public interface RoomJpaRepository extends JpaRepository<RoomEntity, Long> {

    /**
     * 캐릭터 ID와 방 번호로 방을 조회
     * 특정 캐릭터의 특정 번호 방을 찾는 데 사용되며,
     * 캐릭터별 방 번호 유니크 제약 조건에 따라 최대 1개의 결과를 반환한다.
     *
     * @param characterId 조회할 캐릭터의 식별자
     * @param roomNumber 조회할 방 번호
     * @return 해당 조건을 만족하는 RoomEntity (존재하지 않으면 empty)
     */
    Optional<RoomEntity> findByCharacterIdAndRoomNumber(Long characterId, Byte roomNumber);

    /**
     * 특정 캐릭터가 소유한 모든 방을 방 번호 순으로 조회
     * 캐릭터의 방 목록을 표시하거나 방 개수를 확인할 때 사용된다.
     * 결과는 방 번호 오름차순으로 정렬되어 반환된다.
     *
     * @param characterId 조회할 캐릭터의 식별자
     * @return 해당 캐릭터가 소유한 RoomEntity 목록 (방 번호 순 정렬, 없으면 빈 리스트)
     */
    @Query("SELECT r FROM RoomEntity r WHERE r.characterId = :characterId ORDER BY r.roomNumber ASC")
    List<RoomEntity> findByCharacterId(@Param("characterId") Long characterId);
}