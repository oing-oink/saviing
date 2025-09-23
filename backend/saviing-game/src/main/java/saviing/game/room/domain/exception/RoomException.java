package saviing.game.room.domain.exception;

import saviing.common.exception.BusinessException;
import saviing.common.exception.ErrorCode;
import saviing.game.room.domain.model.vo.RoomNumber;

/**
 * Room 도메인에서 발생하는 예외의 기본 클래스입니다.
 */
public abstract class RoomException extends BusinessException {

    /**
     * RoomException 생성자입니다.
     *
     * @param errorCode 에러 코드
     */
    protected RoomException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * RoomException 생성자입니다.
     *
     * @param errorCode 에러 코드
     * @param message 예외 메시지
     */
    protected RoomException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * RoomException 생성자입니다.
     *
     * @param errorCode 에러 코드
     * @param cause 원인 예외
     */
    protected RoomException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    /**
     * 방을 찾을 수 없는 경우 발생하는 예외를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param roomNumber 방 번호
     * @return RoomNotFoundException 인스턴스
     */
    public static RoomNotFoundException notFound(Long characterId, RoomNumber roomNumber) {
        return new RoomNotFoundException(characterId, roomNumber);
    }
}