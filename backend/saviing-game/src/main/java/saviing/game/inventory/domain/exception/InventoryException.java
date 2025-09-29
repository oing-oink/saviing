package saviing.game.inventory.domain.exception;

import saviing.common.exception.BusinessException;

/**
 * 인벤토리 도메인 기본 예외 클래스
 * 모든 인벤토리 관련 예외의 부모 클래스입니다.
 */
public class InventoryException extends BusinessException {

    public InventoryException(InventoryErrorCode errorCode) {
        super(errorCode);
    }

    public InventoryException(InventoryErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public InventoryException(InventoryErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}