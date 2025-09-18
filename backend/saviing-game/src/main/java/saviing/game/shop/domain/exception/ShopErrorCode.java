package saviing.game.shop.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import saviing.common.exception.ErrorCode;

/**
 * 상점 도메인의 에러 코드를 정의하는 열거형입니다.
 */
@Getter
@AllArgsConstructor
public enum ShopErrorCode implements ErrorCode {

    // 구매 조회 관련
    PURCHASE_NOT_FOUND(HttpStatus.NOT_FOUND, "구매 기록을 찾을 수 없습니다"),

    // 구매 요청 관련
    PURCHASE_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "구매하려는 아이템을 찾을 수 없습니다"),
    PURCHASE_INSUFFICIENT_FUNDS(HttpStatus.BAD_REQUEST, "잔액이 부족합니다"),
    PURCHASE_INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "지원하지 않는 결제 수단입니다"),
    PURCHASE_ITEM_UNAVAILABLE(HttpStatus.BAD_REQUEST, "판매 중단된 아이템입니다"),

    // 구매 검증 관련
    PURCHASE_INVALID_CHARACTER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 캐릭터 ID입니다"),
    PURCHASE_INVALID_ITEM_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 아이템 ID입니다"),
    PURCHASE_COIN_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "코인으로 구매할 수 없는 아이템입니다"),
    PURCHASE_FISH_COIN_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "피쉬 코인으로 구매할 수 없는 아이템입니다"),

    // 구매 처리 관련
    PURCHASE_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "구매 처리 중 오류가 발생했습니다"),
    PURCHASE_ITEM_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "아이템 검증에 실패했습니다"),
    PURCHASE_FUNDS_DEBIT_FAILED(HttpStatus.BAD_REQUEST, "자금 차감에 실패했습니다"),
    PURCHASE_ITEM_GRANT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "아이템 지급에 실패했습니다"),
    PURCHASE_REFUND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "환불 처리에 실패했습니다"),

    // 구매 상태 관련
    PURCHASE_INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "잘못된 상태 전이입니다"),
    PURCHASE_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 완료된 구매입니다"),
    PURCHASE_ALREADY_FAILED(HttpStatus.CONFLICT, "이미 실패한 구매입니다")

    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }
}