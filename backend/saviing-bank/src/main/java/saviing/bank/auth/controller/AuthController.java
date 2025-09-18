package saviing.bank.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import saviing.bank.auth.dto.LoginResponse;
import saviing.bank.auth.dto.RefreshResponse;
import saviing.bank.auth.oauth2.service.OAuth2TokenService;
import saviing.bank.auth.oauth2.service.LoginResponseWithCookie;
import saviing.bank.auth.oauth2.service.RefreshResponseWithCookie;
import saviing.common.response.ApiResult;
import saviing.common.exception.BusinessException;
import saviing.common.exception.GlobalErrorCode;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuth2TokenService oAuth2TokenService;

    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@RequestParam("code") String code) {
        // Authorization Code 유효성 검증
        if (code.trim().isEmpty()) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE, "Authorization code는 필수입니다.");
        }

        // OAuth2 로그인 처리
        LoginResponseWithCookie result = oAuth2TokenService.processLogin(code);

        return ApiResult.ok(result.loginResponse())
            .cookie(result.refreshTokenCookie());
    }

    @PostMapping("/refresh")
    public ApiResult<RefreshResponse> refresh(@CookieValue("refresh_token") String refreshToken) {
        // Refresh Token을 사용하여 새로운 토큰 발급
        RefreshResponseWithCookie result = oAuth2TokenService.refreshTokens(refreshToken);

        return ApiResult.ok(result.refreshResponse())
            .cookie(result.refreshTokenCookie());
    }

    @PostMapping("/logout")
    public ApiResult<Void> logout(@CookieValue("refresh_token") String refreshToken) {
        // Refresh Token 검증 및 쿠키 삭제
        ResponseCookie expiredCookie = oAuth2TokenService.logout(refreshToken);

        return ApiResult.ok(null)
            .cookie(expiredCookie);
    }
}