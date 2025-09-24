package saviing.game.room.domain.exception;

import saviing.game.room.domain.model.vo.RoomNumber;

/**
 * 방을 찾을 수 없는 경우 발생하는 예외입니다.
 */
public class RoomNotFoundException extends RoomException {

    private static final String MESSAGE_FORMAT = "캐릭터 ID %d의 방 번호 %d를 찾을 수 없습니다";

    /**
     * RoomNotFoundException 생성자입니다.
     *
     * @param characterId 캐릭터 ID
     * @param roomNumber 방 번호
     */
    public RoomNotFoundException(Long characterId, RoomNumber roomNumber) {
        super(RoomErrorCode.ROOM_NOT_FOUND, String.format(MESSAGE_FORMAT, characterId, roomNumber.value()));
    }
}