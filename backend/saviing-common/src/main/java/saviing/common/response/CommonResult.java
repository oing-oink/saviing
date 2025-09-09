package saviing.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class CommonResult {

    protected final boolean success;
    protected final int status;
    
    @JsonIgnore
    protected HttpStatus statusCode = HttpStatus.OK;
    @JsonIgnore
    protected final Map<String, String> headers = new LinkedHashMap<>();
    @JsonIgnore
    protected final List<ResponseCookie> cookies = new ArrayList<>();

    protected CommonResult(boolean success, int status) {
        this.success = success;
        this.status = status;
    }

    protected CommonResult(boolean success, HttpStatus statusCode) {
        this.success = success;
        this.status = statusCode.value();
        this.statusCode = statusCode;
    }

    // Builder Pattern Methods
    @SuppressWarnings("unchecked")
    public <T extends CommonResult> T header(String key, String value) {
        if (key != null && value != null) {
            this.headers.put(key, value);
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends CommonResult> T headers(Map<String, String> headers) {
        if (headers != null) {
            this.headers.putAll(headers);
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends CommonResult> T cookie(ResponseCookie cookie) {
        if (cookie != null) {
            this.cookies.add(cookie);
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends CommonResult> T cookie(String name, String value) {
        if (name != null) {
            this.cookies.add(ResponseCookie.from(name, value == null ? "" : value).build());
        }
        return (T) this;
    }

    // Getters for JSON serialization
    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    // Getters for HTTP response control (used by ResponseBodyAdvice)
    @JsonIgnore
    public HttpStatus getStatusCode() {
        return statusCode;
    }

    @JsonIgnore
    public Map<String, String> getHeaders() {
        return headers;
    }

    @JsonIgnore
    public List<ResponseCookie> getCookies() {
        return cookies;
    }
}