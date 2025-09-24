package saviing.game.room.domain.exception;

import saviing.common.exception.BusinessException;

/**
 * 배치 겹침으로 인해 발생하는 예외입니다.
 */
public class PlacementOverlapException extends BusinessException {

    /**
     * PlacementOverlapException 생성자입니다.
     *
     * @param message 예외 메시지
     */
    public PlacementOverlapException(String message) {
        super(RoomErrorCode.PLACEMENT_OVERLAP, message);
    }

    /**
     * PlacementOverlapException 생성자입니다.
     *
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public PlacementOverlapException(String message, Throwable cause) {
        super(RoomErrorCode.PLACEMENT_OVERLAP, message, cause);
    }
}