package saviing.bank.account.adapter.in.web.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import saviing.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum WebErrorCode implements ErrorCode {
    
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "계좌를 찾을 수 없습니다"),
    ACCOUNT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 계좌입니다"),
    
    ACCOUNT_INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다"),
    ACCOUNT_INVALID_ACCOUNT_STATE(HttpStatus.BAD_REQUEST, "계좌 상태가 유효하지 않습니다"),
    ACCOUNT_INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "유효하지 않은 금액입니다"),
    ACCOUNT_INVALID_RATE(HttpStatus.BAD_REQUEST, "유효하지 않은 금리입니다"),
    ACCOUNT_INVALID_PRODUCT_TYPE(HttpStatus.BAD_REQUEST, "요청한 계좌 타입과 상품 타입이 일치하지 않습니다"),
    ACCOUNT_INVALID_SAVINGS_TERM(HttpStatus.BAD_REQUEST, "유효하지 않은 적금 기간입니다"),
    ACCOUNT_INVALID_TARGET_AMOUNT(HttpStatus.BAD_REQUEST, "유효하지 않은 목표금액입니다");
    
    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getCode() {
        return name();
    }
}