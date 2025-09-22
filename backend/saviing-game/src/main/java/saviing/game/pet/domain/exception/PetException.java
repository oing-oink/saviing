package saviing.game.pet.domain.exception;

import saviing.common.exception.BusinessException;

/**
 * 펫 도메인의 기본 예외 클래스
 * saviing-common의 BusinessException을 상속합니다.
 */
public class PetException extends BusinessException {

    public PetException(PetErrorCode errorCode) {
        super(errorCode);
    }

    public PetException(PetErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public PetException(PetErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public PetException(PetErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}