package saviing.game.shop.domain.exception;

import saviing.game.shop.domain.model.vo.PaymentMethod;

/**
 * 구매 관련 예외입니다.
 * 아이템 구매 과정에서 발생하는 모든 예외를 처리합니다.
 */
public class PurchaseException extends ShopException {

    private PurchaseException(ShopErrorCode errorCode) {
        super(errorCode);
    }

    private PurchaseException(ShopErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    private PurchaseException(ShopErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    private PurchaseException(ShopErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * 아이템을 찾을 수 없는 예외를 생성합니다.
     */
    public static PurchaseException itemNotFound(Long itemId) {
        String message = "구매하려는 아이템을 찾을 수 없습니다: " + itemId;
        return new PurchaseException(ShopErrorCode.PURCHASE_ITEM_NOT_FOUND, message);
    }

    /**
     * 잔액 부족 예외를 생성합니다.
     */
    public static PurchaseException insufficientFunds(Long characterId, PaymentMethod paymentMethod) {
        String message = String.format("잔액이 부족합니다. 캐릭터 %d, 결제수단: %s", characterId, paymentMethod.getCurrency());
        return new PurchaseException(ShopErrorCode.PURCHASE_INSUFFICIENT_FUNDS, message);
    }

    /**
     * 아이템이 판매 중단된 예외를 생성합니다.
     */
    public static PurchaseException itemUnavailable(Long itemId) {
        String message = "판매 중단된 아이템입니다: " + itemId;
        return new PurchaseException(ShopErrorCode.PURCHASE_ITEM_UNAVAILABLE, message);
    }

    /**
     * 결제 수단이 지원되지 않는 예외를 생성합니다.
     */
    public static PurchaseException paymentMethodNotSupported(Long itemId, PaymentMethod paymentMethod) {
        String message = String.format("아이템 %d은 %s로 구매할 수 없습니다", itemId, paymentMethod.getCurrency());
        return new PurchaseException(
            paymentMethod.isCoin() ?
                ShopErrorCode.PURCHASE_COIN_NOT_SUPPORTED :
                ShopErrorCode.PURCHASE_FISH_COIN_NOT_SUPPORTED,
            message
        );
    }

    /**
     * 아이템 검증 실패 예외를 생성합니다.
     */
    public static PurchaseException itemValidationFailed(Long itemId, Throwable cause) {
        String message = "아이템 검증에 실패했습니다: " + itemId;
        return new PurchaseException(ShopErrorCode.PURCHASE_ITEM_VALIDATION_FAILED, message, cause);
    }

    /**
     * 자금 차감 실패 예외를 생성합니다.
     */
    public static PurchaseException fundsDebitFailed(Long characterId, Throwable cause) {
        String message = "자금 차감에 실패했습니다. 캐릭터 " + characterId;
        return new PurchaseException(ShopErrorCode.PURCHASE_FUNDS_DEBIT_FAILED, message, cause);
    }

    /**
     * 아이템 지급 실패 예외를 생성합니다.
     */
    public static PurchaseException itemGrantFailed(Long characterId, Long itemId, Throwable cause) {
        String message = String.format("아이템 지급에 실패했습니다. 캐릭터 %d, 아이템 %d", characterId, itemId);
        return new PurchaseException(ShopErrorCode.PURCHASE_ITEM_GRANT_FAILED, message, cause);
    }

    /**
     * 구매 처리 실패 예외를 생성합니다.
     */
    public static PurchaseException processingFailed(String reason, Throwable cause) {
        String message = "구매 처리 중 오류가 발생했습니다: " + reason;
        return new PurchaseException(ShopErrorCode.PURCHASE_PROCESSING_FAILED, message, cause);
    }
}