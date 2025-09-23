package saviing.game.room.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Room 도메인 객체의 JPA 엔티티
 * 데이터베이스의 room 테이블과 매핑되며,
 * 도메인 객체와 데이터베이스 간의 변환을 담당한다.
 */
@Entity
@Table(
    name = "room",
    indexes = {
        @Index(name = "idx_character_id", columnList = "characterId"),
        @Index(name = "uk_character_room", columnList = "characterId, roomNumber", unique = true)
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false)
    private Long characterId;

    @Column(nullable = false)
    private Byte roomNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * RoomEntity 생성자
     * 새로운 방 엔티티를 생성할 때 사용되며, roomId는 데이터베이스에서 자동 생성된다.
     *
     * @param characterId 방을 소유할 캐릭터의 식별자
     * @param roomNumber 방 번호 (1-5 범위)
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     */
    public RoomEntity(Long characterId, Byte roomNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.characterId = characterId;
        this.roomNumber = roomNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 완전한 RoomEntity 생성자
     * 데이터베이스에서 조회한 데이터로 엔티티를 복원할 때 사용된다.
     *
     * @param roomId 방 식별자
     * @param characterId 캐릭터 식별자
     * @param roomNumber 방 번호
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     */
    public RoomEntity(Long roomId, Long characterId, Byte roomNumber,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.roomId = roomId;
        this.characterId = characterId;
        this.roomNumber = roomNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 수정 시각을 현재 시각으로 업데이트
     * 엔티티의 정보가 변경될 때 호출되어 수정 시각을 갱신한다.
     */
    public void updateModifiedTime() {
        this.updatedAt = LocalDateTime.now();
    }
}