package saviing.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import saviing.common.response.ErrorResult;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ErrorResult handleBusinessException(BusinessException e) {
        log.error("Business exception occurred: {}", e.getMessage(), e);
        ErrorCode errorCode = e.getErrorCode();
        
        return ErrorResult.of(errorCode, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResult handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation exception occurred: {}", e.getMessage(), e);
        
        ErrorResult errorResult = ErrorResult.validationError(ErrorCode.INVALID_INPUT_VALUE);
        
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorResult.addInvalidParam(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue()
            );
        }
        
        return errorResult;
    }

    @ExceptionHandler(BindException.class)
    public ErrorResult handleBindException(BindException e) {
        log.error("Bind exception occurred: {}", e.getMessage(), e);
        
        ErrorResult errorResult = ErrorResult.validationError(ErrorCode.INVALID_INPUT_VALUE);
        
        for (FieldError fieldError : e.getFieldErrors()) {
            errorResult.addInvalidParam(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue()
            );
        }
        
        return errorResult;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResult handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("Type mismatch exception occurred: {}", e.getMessage(), e);
        
        return ErrorResult.validationError(ErrorCode.INVALID_TYPE_VALUE)
            .addInvalidParam(
                e.getName(),
                "올바른 타입으로 입력해주세요.",
                e.getValue()
            );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResult handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Http message not readable exception occurred: {}", e.getMessage(), e);
        
        return ErrorResult.of(ErrorCode.INVALID_DATA_FORMAT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument exception occurred: {}", e.getMessage(), e);
        
        return ErrorResult.of(ErrorCode.INVALID_INPUT_VALUE, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResult handleGeneralException(Exception e) {
        log.error("Unexpected exception occurred: {}", e.getMessage(), e);
        
        return ErrorResult.of(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}