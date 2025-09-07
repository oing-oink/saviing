package saviing.common.response;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class ApiResult<T> extends CommonResult {

    private T body;

    private ApiResult(boolean success, int status) {
        super(success, status);
    }

    private ApiResult(boolean success, int status, T body) {
        super(success, status);
        this.body = body;
    }

    private ApiResult(boolean success, HttpStatus statusCode, T body) {
        super(success, statusCode);
        this.body = body;
    }

    // Static Factory Methods
    public static <T> ApiResult<T> ok() {
        return new ApiResult<>(true, HttpStatus.OK.value());
    }

    public static <T> ApiResult<T> ok(T body) {
        return new ApiResult<>(true, HttpStatus.OK.value(), body);
    }

    public static <T> ApiResult<T> of(int status, T body) {
        ApiResult<T> result = new ApiResult<>(true, status, body);
        result.statusCode = HttpStatus.valueOf(status);
        return result;
    }

    public static <T> ApiResult<T> of(HttpStatus status, T body) {
        return new ApiResult<>(true, status, body);
    }

    public ApiResult<T> body(T body) {
        this.body = body;
        return this;
    }
}