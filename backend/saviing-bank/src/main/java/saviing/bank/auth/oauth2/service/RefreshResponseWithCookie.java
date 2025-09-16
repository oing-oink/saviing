package saviing.bank.auth.oauth2.service;

import org.springframework.http.ResponseCookie;
import saviing.bank.auth.dto.RefreshResponse;

/**
 * Refresh Token 갱신 응답과 새로운 Refresh Token 쿠키를 함께 담는 래퍼 클래스
 */
public record RefreshResponseWithCookie(
    RefreshResponse refreshResponse,
    ResponseCookie refreshTokenCookie
) {
}