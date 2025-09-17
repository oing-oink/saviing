package saviing.bank.transaction.adapter.in.web.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import saviing.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum TransactionWebErrorCode implements ErrorCode {

    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "거래를 찾을 수 없습니다"),
    INVALID_TRANSACTION_AMOUNT(HttpStatus.BAD_REQUEST, "유효하지 않은 거래 금액입니다"),
    DUPLICATE_TRANSACTION(HttpStatus.CONFLICT, "중복된 거래입니다"),
    INVALID_TRANSACTION_STATE(HttpStatus.BAD_REQUEST, "유효하지 않은 거래 상태입니다"),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다"),
    INVALID_ACCOUNT_STATE(HttpStatus.BAD_REQUEST, "유효하지 않은 계좌 상태입니다"),
    INVALID_VALUE_DATE(HttpStatus.BAD_REQUEST, "유효하지 않은 가치일입니다"),
    TRANSACTION_ALREADY_VOID(HttpStatus.BAD_REQUEST, "이미 무효화된 거래입니다"),
    DUPLICATE_TRANSFER(HttpStatus.CONFLICT, "중복된 송금입니다"),
    TRANSFER_IN_PROGRESS(HttpStatus.CONFLICT, "송금이 처리 중입니다"),
    LEDGER_NOT_FOUND(HttpStatus.NOT_FOUND, "송금 원장을 찾을 수 없습니다"),
    INVALID_LEDGER_STATE(HttpStatus.BAD_REQUEST, "유효하지 않은 송금 원장 상태입니다"),
    ACCOUNT_API_FAILURE(HttpStatus.BAD_GATEWAY, "계좌 서비스 호출에 실패했습니다"),
    TRANSFER_VALIDATION(HttpStatus.UNPROCESSABLE_ENTITY, "송금 검증에 실패했습니다");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getCode() {
        return name();
    }
}
