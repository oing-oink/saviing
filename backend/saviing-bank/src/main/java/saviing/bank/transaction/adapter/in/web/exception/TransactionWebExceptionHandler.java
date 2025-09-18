package saviing.bank.transaction.adapter.in.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import saviing.bank.transaction.exception.TransactionErrorType;
import saviing.bank.transaction.exception.TransactionException;
import saviing.common.response.ErrorResult;

/**
 * Transaction 도메인 예외를 HTTP 응답으로 변환하는 전역 예외 처리기.
 */
@Slf4j
@RestControllerAdvice
public class TransactionWebExceptionHandler {

    @ExceptionHandler(TransactionException.class)
    /**
     * 도메인 예외를 받아 공통 에러 포맷으로 변환한다.
     */
    public ErrorResult handleDomainException(TransactionException e) {
        TransactionWebErrorCode errorCode = mapToWebErrorCode(e.getErrorType());

        if (e.getContext().isEmpty()) {
            log.warn("Transaction 도메인 에러 [{}]: {}", e.getErrorType(), e.getMessage());
        } else {
            log.warn("Transaction 도메인 에러 [{}]: {} - Context: {}", e.getErrorType(), e.getMessage(), e.getContext());
        }

        return ErrorResult.of(errorCode, errorCode.getMessage());
    }

    private TransactionWebErrorCode mapToWebErrorCode(TransactionErrorType errorType) {
        return switch (errorType) {
            case TRANSACTION_NOT_FOUND -> TransactionWebErrorCode.TRANSACTION_NOT_FOUND;
            case INVALID_TRANSACTION_AMOUNT -> TransactionWebErrorCode.INVALID_TRANSACTION_AMOUNT;
            case DUPLICATE_TRANSACTION -> TransactionWebErrorCode.DUPLICATE_TRANSACTION;
            case INVALID_TRANSACTION_STATE -> TransactionWebErrorCode.INVALID_TRANSACTION_STATE;
            case INSUFFICIENT_BALANCE -> TransactionWebErrorCode.INSUFFICIENT_BALANCE;
            case INVALID_ACCOUNT_STATE -> TransactionWebErrorCode.INVALID_ACCOUNT_STATE;
            case INVALID_VALUE_DATE -> TransactionWebErrorCode.INVALID_VALUE_DATE;
            case TRANSACTION_ALREADY_VOID -> TransactionWebErrorCode.TRANSACTION_ALREADY_VOID;
            case DUPLICATE_TRANSFER -> TransactionWebErrorCode.DUPLICATE_TRANSFER;
            case TRANSFER_IN_PROGRESS -> TransactionWebErrorCode.TRANSFER_IN_PROGRESS;
            case LEDGER_NOT_FOUND -> TransactionWebErrorCode.LEDGER_NOT_FOUND;
            case INVALID_LEDGER_STATE -> TransactionWebErrorCode.INVALID_LEDGER_STATE;
            case ACCOUNT_API_FAILURE -> TransactionWebErrorCode.ACCOUNT_API_FAILURE;
            case TRANSFER_VALIDATION -> TransactionWebErrorCode.TRANSFER_VALIDATION;
        };
    }
}
