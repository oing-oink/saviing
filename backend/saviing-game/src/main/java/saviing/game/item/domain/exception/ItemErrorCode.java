package saviing.game.item.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import saviing.common.exception.ErrorCode;

/**
 * 아이템 도메인의 에러 코드를 정의하는 열거형
 */
@Getter
@AllArgsConstructor
public enum ItemErrorCode implements ErrorCode {

    // 아이템 조회 관련
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "아이템을 찾을 수 없습니다"),

    // 아이템 등록/수정 관련
    ITEM_INVALID_NAME(HttpStatus.BAD_REQUEST, "올바르지 않은 아이템 이름입니다"),
    ITEM_INVALID_DESCRIPTION(HttpStatus.BAD_REQUEST, "올바르지 않은 아이템 설명입니다"),
    ITEM_INVALID_SIZE(HttpStatus.BAD_REQUEST, "올바르지 않은 아이템 크기입니다"),
    ITEM_INVALID_PRICE(HttpStatus.BAD_REQUEST, "올바르지 않은 아이템 가격입니다"),
    ITEM_INVALID_IMAGE_URL(HttpStatus.BAD_REQUEST, "올바르지 않은 이미지 URL입니다"),
    ITEM_INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "올바르지 않은 아이템 카테고리입니다"),

    // 아이템 상태 관련
    ITEM_ALREADY_AVAILABLE(HttpStatus.CONFLICT, "이미 판매 중인 아이템입니다"),
    ITEM_ALREADY_UNAVAILABLE(HttpStatus.CONFLICT, "이미 판매 중단된 아이템입니다"),

    // 아이템 정의 관련
    ITEM_INVALID_DEFINITION(HttpStatus.BAD_REQUEST, "잘못된 아이템 정의입니다"),
    ITEM_CATEGORY_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "아이템 카테고리와 타입이 일치하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }
}