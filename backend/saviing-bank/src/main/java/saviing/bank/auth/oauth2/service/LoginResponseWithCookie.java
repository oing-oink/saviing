package saviing.bank.auth.oauth2.service;

import org.springframework.http.ResponseCookie;
import saviing.bank.auth.dto.LoginResponse;

/**
 * 로그인 응답과 리프레시 토큰 쿠키를 함께 담는 래퍼 클래스
 */
public record LoginResponseWithCookie(
    LoginResponse loginResponse,
    ResponseCookie refreshTokenCookie
) {
}