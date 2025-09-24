package saviing.game.room.domain.exception;

import saviing.common.exception.BusinessException;

/**
 * 유효하지 않은 배치 정보로 인해 발생하는 예외입니다.
 */
public class InvalidPlacementException extends BusinessException {

    /**
     * InvalidPlacementException 생성자입니다.
     *
     * @param message 예외 메시지
     */
    public InvalidPlacementException(String message) {
        super(RoomErrorCode.INVALID_PLACEMENT, message);
    }

    /**
     * InvalidPlacementException 생성자입니다.
     *
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public InvalidPlacementException(String message, Throwable cause) {
        super(RoomErrorCode.INVALID_PLACEMENT, message, cause);
    }
}