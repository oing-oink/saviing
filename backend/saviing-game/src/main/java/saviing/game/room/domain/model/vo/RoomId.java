package saviing.game.room.domain.model.vo;

import lombok.NonNull;

/**
 * 방(Room) 식별자를 나타내는 값 객체
 *
 * @param value 방 식별자 값. 양수여야 함
 */
public record RoomId(@NonNull Long value) {

    /**
     * RoomId 생성자
     *
     * @param value 방 식별자 값
     * @throws IllegalArgumentException value가 null이거나 0 이하인 경우
     */
    public RoomId {
        if (value <= 0) {
            throw new IllegalArgumentException("roomId must be positive");
        }
    }
}