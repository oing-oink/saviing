package saviing.game.shop.domain.exception;

import saviing.common.exception.BusinessException;

/**
 * 상점 도메인 예외의 기본 클래스입니다.
 * saviing-common의 BusinessException을 상속합니다.
 */
public class ShopException extends BusinessException {

    public ShopException(ShopErrorCode errorCode) {
        super(errorCode);
    }

    public ShopException(ShopErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ShopException(ShopErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ShopException(ShopErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}