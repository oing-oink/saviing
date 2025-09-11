package saviing.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import saviing.common.response.ErrorResult;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ErrorResult handleBusinessException(BusinessException e) {
        log.info("{}: {} - {}", e.getClass().getSimpleName(), e.getErrorCode(), e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        
        return ErrorResult.of(errorCode, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResult handleValidationException(MethodArgumentNotValidException e) {
        ErrorResult errorResult = ErrorResult.validationError(GlobalErrorCode.INVALID_INPUT_VALUE);
        
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
        ErrorResult errorResult = ErrorResult.validationError(GlobalErrorCode.INVALID_INPUT_VALUE);
        
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
        return ErrorResult.validationError(GlobalErrorCode.INVALID_TYPE_VALUE)
            .addInvalidParam(
                e.getName(),
                "올바른 타입으로 입력해주세요.",
                e.getValue()
            );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResult handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ErrorResult.of(GlobalErrorCode.INVALID_DATA_FORMAT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult handleIllegalArgumentException(IllegalArgumentException e) {
        return ErrorResult.of(GlobalErrorCode.INVALID_INPUT_VALUE, e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ErrorResult handleNoResourceFoundException(NoResourceFoundException e) {
        return ErrorResult.of(GlobalErrorCode.ENDPOINT_NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ErrorResult handleGeneralException(Exception e) {
        log.error("예상치 못한 오류: {}", e.getMessage(), e);
        
        return ErrorResult.of(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }
}