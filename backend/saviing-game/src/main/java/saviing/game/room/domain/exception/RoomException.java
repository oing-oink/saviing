package saviing.game.room.domain.exception;

/**
 * Room 도메인에서 발생하는 예외의 기본 클래스입니다.
 */
public abstract class RoomException extends RuntimeException {

    /**
     * RoomException 생성자입니다.
     *
     * @param message 예외 메시지
     */
    protected RoomException(String message) {
        super(message);
    }

    /**
     * RoomException 생성자입니다.
     *
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    protected RoomException(String message, Throwable cause) {
        super(message, cause);
    }
}