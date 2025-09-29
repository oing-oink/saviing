package saviing.game.room.application.dto.result;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.NonNull;
import saviing.game.room.domain.model.aggregate.Room;

/**
 * 방 생성 결과 DTO
 * 방 생성 작업이 완료된 후 반환되는 결과 데이터를 담는 불변 객체이다.
 * 생성된 방의 기본 정보와 생성 시각을 포함하여 클라이언트에게 제공한다.
 *
 * @param roomId 생성된 방의 식별자
 * @param characterId 방을 소유하는 캐릭터의 식별자
 * @param roomNumber 생성된 방의 번호
 * @param createdAt 방이 생성된 시각
 */
@Builder
public record RoomCreatedResult(
    Long roomId,
    Long characterId,
    Byte roomNumber,
    LocalDateTime createdAt
) {

    /**
     * Room 도메인 객체로부터 RoomCreatedResult를 생성하는 정적 팩토리 메서드
     * 도메인 객체의 데이터를 DTO로 변환하여 안전한 인스턴스를 생성한다.
     *
     * @param room 생성된 Room 도메인 객체
     * @return 생성된 RoomCreatedResult 인스턴스
     * @throws IllegalArgumentException room이 null인 경우
     */
    public static RoomCreatedResult from(@NonNull Room room) {
        return RoomCreatedResult.builder()
            .roomId(room.getRoomId().value())
            .characterId(room.getCharacterId())
            .roomNumber(room.getRoomNumber().value())
            .createdAt(room.getCreatedAt())
            .build();
    }

}