package saviing.bank.account.adapter.in.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import saviing.bank.account.exception.AccountErrorType;
import saviing.bank.account.exception.AccountException;
import saviing.common.response.ErrorResult;

@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ErrorResult handleDomainException(AccountException e) {
        WebErrorCode errorCode = mapToWebErrorCode(e.getErrorType());

        if (e.getContext().isEmpty()) {
            log.warn("Account 도메인 에러 [{}]: {}", e.getErrorType(), e.getMessage());
        } else {
            log.warn("Account 도메인 에러 [{}]: {} - Context: {}", e.getErrorType(), e.getMessage(), e.getContext());
        }

        return ErrorResult.of(errorCode, errorCode.getMessage());
    }

    private WebErrorCode mapToWebErrorCode(AccountErrorType errorType) {
        return switch (errorType) {
            case ACCOUNT_NOT_FOUND -> WebErrorCode.ACCOUNT_NOT_FOUND;
            case INSUFFICIENT_BALANCE -> WebErrorCode.ACCOUNT_INSUFFICIENT_BALANCE;
            case INVALID_AMOUNT -> WebErrorCode.ACCOUNT_INVALID_AMOUNT;
            case INVALID_ACCOUNT_STATE -> WebErrorCode.ACCOUNT_INVALID_ACCOUNT_STATE;
            case INVALID_RATE -> WebErrorCode.ACCOUNT_INVALID_RATE;
            case INVALID_PRODUCT_TYPE -> WebErrorCode.ACCOUNT_INVALID_PRODUCT_TYPE;
            case INVALID_SAVINGS_TERM -> WebErrorCode.ACCOUNT_INVALID_SAVINGS_TERM;
            case INVALID_TARGET_AMOUNT -> WebErrorCode.ACCOUNT_INVALID_TARGET_AMOUNT;
            case INVALID_WITHDRAWAL_ACCOUNT -> WebErrorCode.ACCOUNT_INVALID_WITHDRAWAL_ACCOUNT;
        };
    }

}
