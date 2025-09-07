package saviing.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    
    // 기본 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "입력값이 올바르지 않습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "INVALID_TYPE_VALUE", "입력 타입이 올바르지 않습니다."),
    INVALID_DATA_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_DATA_FORMAT", "요청 데이터 형식이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "허용되지 않은 HTTP 메소드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근이 거부되었습니다."),
    
    /*
     * 도메인별 에러 [DOMAIN_NAME]_[ERROR_CODE]
     * 예) BANK_ACCOUNT_NOT_FOUND
     */

    // 은행 도메인 에러
    BANK_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "BANK_ACCOUNT_NOT_FOUND", "계좌를 찾을 수 없습니다."),
    BANK_ACCOUNT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "BANK_ACCOUNT_ALREADY_EXISTS", "계좌가 이미 존재합니다."),
    ;
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    
    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}