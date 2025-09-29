package saviing.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import saviing.common.exception.ErrorCode;


@Getter
public class ErrorResult extends CommonResult {

    private String code;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss XXX'['VV']'")
    private ZonedDateTime timestamp;
    private List<InvalidParam> invalidParams;

    private ErrorResult(int status, String code, String message) {
        super(false, status);
        this.code = code;
        this.message = message;
        this.timestamp = ZonedDateTime.now();
    }

    private ErrorResult(HttpStatus statusCode, String code, String message) {
        super(false, statusCode);
        this.code = code;
        this.message = message;
        this.timestamp = ZonedDateTime.now();
    }

    // Static Factory Methods with ErrorCode
    public static ErrorResult of(ErrorCode errorCode) {
        return new ErrorResult(errorCode.getHttpStatus(), errorCode.getCode(), errorCode.getMessage());
    }

    public static ErrorResult of(ErrorCode errorCode, String customMessage) {
        return new ErrorResult(errorCode.getHttpStatus(), errorCode.getCode(), customMessage);
    }


    public static ErrorResult validationError(ErrorCode errorCode) {
        ErrorResult result = new ErrorResult(errorCode.getHttpStatus(), errorCode.getCode(), errorCode.getMessage());
        result.invalidParams = new ArrayList<>();
        return result;
    }

    public static ErrorResult validationError(ErrorCode errorCode, String customMessage) {
        ErrorResult result = new ErrorResult(errorCode.getHttpStatus(), errorCode.getCode(), customMessage);
        result.invalidParams = new ArrayList<>();
        return result;
    }


    // Builder Methods
    public ErrorResult addInvalidParam(String field, String message, Object rejectedValue) {
        if (this.invalidParams == null) {
            this.invalidParams = new ArrayList<>();
        }
        this.invalidParams.add(InvalidParam.builder()
            .field(field)
            .message(message)
            .rejectedValue(rejectedValue)
            .build());
        return this;
    }

    public ErrorResult addInvalidParam(InvalidParam invalidParam) {
        if (this.invalidParams == null) {
            this.invalidParams = new ArrayList<>();
        }
        this.invalidParams.add(invalidParam);
        return this;
    }
}