package saviing.game.room.domain.repository;

import java.util.List;
import java.util.Optional;

import saviing.game.room.domain.model.aggregate.Room;
import saviing.game.room.domain.model.vo.RoomId;
import saviing.game.room.domain.model.vo.RoomNumber;

/**
 * Room Aggregate의 영속성을 담당하는 Repository 인터페이스
 * 도메인 레이어에서 정의되며, 인프라 레이어에서 구현된다.
 * Room의 생성, 조회, 수정 등의 데이터 접근 기능을 추상화한다.
 */
public interface RoomRepository {

    /**
     * Room을 저장하거나 업데이트
     * 새로운 Room인 경우 생성하고, 기존 Room인 경우 수정한다.
     *
     * @param room 저장할 Room 엔티티
     * @return 저장된 Room 엔티티 (ID가 할당된 상태)
     * @throws IllegalArgumentException room이 null인 경우
     */
    Room save(Room room);

    /**
     * 방 식별자로 Room을 조회
     * 특정 방의 상세 정보를 조회할 때 사용한다.
     *
     * @param roomId 조회할 방의 식별자
     * @return 해당 roomId를 가진 Room (존재하지 않으면 empty)
     * @throws IllegalArgumentException roomId가 null인 경우
     */
    Optional<Room> findById(RoomId roomId);

    /**
     * 캐릭터 ID와 방 번호로 Room을 조회
     * 특정 캐릭터의 특정 번호 방을 찾을 때 사용한다.
     * 방 번호는 캐릭터별로 유니크하므로 최대 1개의 결과를 반환한다.
     *
     * @param characterId 조회할 캐릭터의 식별자
     * @param roomNumber 조회할 방 번호
     * @return 해당 조건을 만족하는 Room (존재하지 않으면 empty)
     * @throws IllegalArgumentException characterId가 null이거나 0 이하인 경우
     * @throws IllegalArgumentException roomNumber가 null인 경우
     */
    Optional<Room> findByCharacterIdAndRoomNumber(Long characterId, RoomNumber roomNumber);

    /**
     * 특정 캐릭터가 소유한 모든 방을 조회
     * 캐릭터의 방 목록을 표시하거나, 방 개수를 확인할 때 사용한다.
     * 결과는 방 번호 순으로 정렬되어 반환된다.
     *
     * @param characterId 조회할 캐릭터의 식별자
     * @return 해당 캐릭터가 소유한 Room 목록 (없으면 빈 리스트)
     * @throws IllegalArgumentException characterId가 null이거나 0 이하인 경우
     */
    List<Room> findByCharacterId(Long characterId);
}