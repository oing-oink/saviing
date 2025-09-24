package saviing.game.room.domain.model.aggregate;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import saviing.game.room.domain.model.vo.RoomId;
import saviing.game.room.domain.model.vo.RoomNumber;

/**
 * 방 Aggregate Root
 * 캐릭터별 방 정보를 관리하는 핵심 도메인 객체로,
 * 방의 생성, 수정 등의 비즈니스 규칙을 담당한다.
 * 각 방은 고유한 식별자와 캐릭터 연관 관계를 가진다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {

    private RoomId roomId;
    private Long characterId;
    private RoomNumber roomNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Room 생성자 (내부용)
     * 도메인 객체의 불변성을 보장하기 위해 private으로 제한
     *
     * @param roomId 방 식별자
     * @param characterId 캐릭터 식별자
     * @param roomNumber 방 번호
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     */
    private Room(RoomId roomId, Long characterId, RoomNumber roomNumber,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.roomId = roomId;
        this.characterId = characterId;
        this.roomNumber = roomNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 새로운 방을 생성하는 정적 팩토리 메서드
     * 방 생성 시 현재 시각을 생성/수정 시각으로 설정하며,
     * RoomId는 null로 초기화하여 저장 시 자동 생성되도록 한다.
     *
     * @param characterId 방을 소유할 캐릭터의 식별자
     * @param roomNumber 생성할 방의 번호
     * @return 생성된 Room 인스턴스
     * @throws IllegalArgumentException characterId가 null이거나 0 이하인 경우
     * @throws IllegalArgumentException roomNumber가 null인 경우
     */
    public static Room create(
        @NonNull Long characterId,
        @NonNull RoomNumber roomNumber
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new Room(null, characterId, roomNumber, now, now);
    }

    /**
     * 기존 방 정보로 Room 인스턴스를 복원하는 정적 팩토리 메서드
     * 데이터베이스에서 조회한 데이터를 도메인 객체로 변환할 때 사용
     *
     * @param roomId 방 식별자
     * @param characterId 캐릭터 식별자
     * @param roomNumber 방 번호
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 Room 인스턴스
     * @throws IllegalArgumentException 필수 파라미터가 null이거나 잘못된 경우
     */
    public static Room restore(
        @NonNull RoomId roomId,
        @NonNull Long characterId,
        @NonNull RoomNumber roomNumber,
        @NonNull LocalDateTime createdAt,
        @NonNull LocalDateTime updatedAt
    ) { 

        return new Room(roomId, characterId, roomNumber, createdAt, updatedAt);
    }

    /**
     * 방 정보가 수정되었음을 표시하고 수정 시각을 갱신
     * 방 관련 정보가 변경될 때 호출되어야 하며,
     * 엔티티의 생명주기를 추적하는 역할을 한다.
     */
    public void markAsUpdated() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 두 Room 객체가 같은지 비교
     * roomId를 기준으로 동등성을 판단한다.
     *
     * @param obj 비교할 객체
     * @return 같은 roomId를 가지면 true, 그렇지 않으면 false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Room room = (Room) obj;
        return Objects.equals(roomId, room.roomId);
    }
}