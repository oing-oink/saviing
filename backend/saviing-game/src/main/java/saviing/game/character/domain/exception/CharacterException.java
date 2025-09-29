package saviing.game.character.domain.exception;

import saviing.common.exception.BusinessException;

/**
 * 캐릭터 도메인의 기본 예외 클래스
 * saviing-common의 BusinessException을 상속합니다.
 */
public class CharacterException extends BusinessException {
    
    public CharacterException(CharacterErrorCode errorCode) {
        super(errorCode);
    }
    
    public CharacterException(CharacterErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public CharacterException(CharacterErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
    
    public CharacterException(CharacterErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}