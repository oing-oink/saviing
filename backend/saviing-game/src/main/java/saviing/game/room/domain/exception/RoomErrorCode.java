package saviing.game.room.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import saviing.common.exception.ErrorCode;

/**
 * Room 도메인의 에러 코드를 정의하는 열거형입니다.
 */
@Getter
@AllArgsConstructor
public enum RoomErrorCode implements ErrorCode {

    INVALID_PLACEMENT(HttpStatus.BAD_REQUEST, "유효하지 않은 배치 정보입니다"),
    PLACEMENT_OVERLAP(HttpStatus.BAD_REQUEST, "배치가 겹칩니다"),
    PET_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "펫은 최대 2마리까지 배치할 수 있습니다"),
    DUPLICATE_INVENTORY_ITEM(HttpStatus.BAD_REQUEST, "동일한 인벤토리 아이템이 중복 배치되었습니다"),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }
}