package saviing.game.room.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import saviing.game.room.domain.model.aggregate.Room;
import saviing.game.room.domain.model.vo.RoomId;
import saviing.game.room.domain.model.vo.RoomNumber;
import saviing.game.room.domain.repository.RoomRepository;
import saviing.game.room.infrastructure.persistence.entity.RoomEntity;

/**
 * Room Repository의 JPA 구현체
 * 도메인의 RoomRepository 인터페이스를 구현하여
 * 데이터베이스와 도메인 객체 간의 변환 및 영속성을 담당한다.
 */
@Repository
@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepository {

    private final RoomJpaRepository roomJpaRepository;

    /**
     * Room을 저장하거나 업데이트
     * 도메인 객체를 엔티티로 변환하여 데이터베이스에 저장하고,
     * 저장된 결과를 다시 도메인 객체로 변환하여 반환한다.
     *
     * @param room 저장할 Room 도메인 객체
     * @return 저장된 Room 도메인 객체 (ID가 할당된 상태)
     * @throws IllegalArgumentException room이 null인 경우
     */
    @Override
    public Room save(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("저장할 Room은 필수입니다");
        }

        RoomEntity entity = toEntity(room);
        RoomEntity savedEntity = roomJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    /**
     * 방 식별자로 Room을 조회
     * 데이터베이스에서 엔티티를 조회하고 도메인 객체로 변환하여 반환한다.
     *
     * @param roomId 조회할 방의 식별자
     * @return 해당 roomId를 가진 Room (존재하지 않으면 empty)
     * @throws IllegalArgumentException roomId가 null인 경우
     */
    @Override
    public Optional<Room> findById(RoomId roomId) {
        if (roomId == null) {
            throw new IllegalArgumentException("방 식별자는 필수입니다");
        }

        return roomJpaRepository.findById(roomId.value())
            .map(this::toDomain);
    }

    /**
     * 캐릭터 ID와 방 번호로 Room을 조회
     * 특정 캐릭터의 특정 번호 방을 조회하여 도메인 객체로 변환한다.
     *
     * @param characterId 조회할 캐릭터의 식별자
     * @param roomNumber 조회할 방 번호
     * @return 해당 조건을 만족하는 Room (존재하지 않으면 empty)
     * @throws IllegalArgumentException characterId가 null이거나 0 이하인 경우
     * @throws IllegalArgumentException roomNumber가 null인 경우
     */
    @Override
    public Optional<Room> findByCharacterIdAndRoomNumber(Long characterId, RoomNumber roomNumber) {
        validateCharacterId(characterId);
        if (roomNumber == null) {
            throw new IllegalArgumentException("방 번호는 필수입니다");
        }

        return roomJpaRepository.findByCharacterIdAndRoomNumber(characterId, roomNumber.value())
            .map(this::toDomain);
    }

    /**
     * 특정 캐릭터가 소유한 모든 방을 조회
     * 캐릭터의 모든 방을 조회하고 방 번호 순으로 정렬된 도메인 객체 목록을 반환한다.
     *
     * @param characterId 조회할 캐릭터의 식별자
     * @return 해당 캐릭터가 소유한 Room 목록 (방 번호 순 정렬, 없으면 빈 리스트)
     * @throws IllegalArgumentException characterId가 null이거나 0 이하인 경우
     */
    @Override
    public List<Room> findByCharacterId(Long characterId) {
        validateCharacterId(characterId);

        return roomJpaRepository.findByCharacterId(characterId)
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    /**
     * Room 도메인 객체를 RoomEntity로 변환
     * 도메인 객체의 데이터를 JPA 엔티티 형태로 변환한다.
     *
     * @param room 변환할 Room 도메인 객체
     * @return 변환된 RoomEntity
     */
    private RoomEntity toEntity(Room room) {
        if (room.getRoomId() == null) {
            // 새로운 Room인 경우 (ID가 없는 경우)
            return new RoomEntity(
                room.getCharacterId(),
                room.getRoomNumber().value(),
                room.getCreatedAt(),
                room.getUpdatedAt()
            );
        } else {
            // 기존 Room인 경우 (ID가 있는 경우)
            return new RoomEntity(
                room.getRoomId().value(),
                room.getCharacterId(),
                room.getRoomNumber().value(),
                room.getCreatedAt(),
                room.getUpdatedAt()
            );
        }
    }

    /**
     * RoomEntity를 Room 도메인 객체로 변환
     * JPA 엔티티의 데이터를 도메인 객체 형태로 변환한다.
     *
     * @param entity 변환할 RoomEntity
     * @return 변환된 Room 도메인 객체
     */
    private Room toDomain(RoomEntity entity) {
        return Room.restore(
            new RoomId(entity.getRoomId()),
            entity.getCharacterId(),
            RoomNumber.of(entity.getRoomNumber()),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * 캐릭터 식별자 유효성 검증
     *
     * @param characterId 검증할 캐릭터 식별자
     * @throws IllegalArgumentException characterId가 null이거나 0 이하인 경우
     */
    private void validateCharacterId(Long characterId) {
        if (characterId == null || characterId <= 0) {
            throw new IllegalArgumentException("캐릭터 식별자는 필수이며 양수여야 합니다");
        }
    }
}