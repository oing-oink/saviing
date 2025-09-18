package saviing.game.item.domain.exception;

import saviing.common.exception.BusinessException;

/**
 * 아이템 도메인의 기본 예외 클래스
 * saviing-common의 BusinessException을 상속합니다.
 */
public class ItemException extends BusinessException {

    public ItemException(ItemErrorCode errorCode) {
        super(errorCode);
    }

    public ItemException(ItemErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ItemException(ItemErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ItemException(ItemErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}