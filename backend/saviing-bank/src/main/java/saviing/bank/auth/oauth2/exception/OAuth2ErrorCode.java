package saviing.bank.auth.oauth2.exception;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import saviing.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum OAuth2ErrorCode implements ErrorCode {

    TOKEN_EXCHANGE_FAILED(HttpStatus.UNAUTHORIZED, "OAUTH2_TOKEN_EXCHANGE_FAILED", "OAuth2 토큰 교환에 실패했습니다."),
    USER_INFO_RETRIEVAL_FAILED(HttpStatus.UNAUTHORIZED, "OAUTH2_USER_INFO_FAILED", "OAuth2 사용자 정보 조회에 실패했습니다."),
    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, "OAUTH2_UNSUPPORTED_PROVIDER", "지원하지 않는 OAuth2 제공자입니다."),
    INVALID_REDIRECT_URI(HttpStatus.BAD_REQUEST, "OAUTH2_INVALID_REDIRECT_URI", "허용되지 않은 redirect URI입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}