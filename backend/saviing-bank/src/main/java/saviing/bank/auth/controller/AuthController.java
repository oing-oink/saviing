package saviing.bank.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import saviing.bank.auth.dto.LoginResponse;
import saviing.bank.auth.oauth2.dto.TokenResponse;
import saviing.bank.auth.oauth2.service.OAuth2TokenService;
import saviing.common.response.ApiResult;
import saviing.common.config.JwtConfig;
import saviing.common.exception.BusinessException;
import saviing.common.exception.GlobalErrorCode;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuth2TokenService oAuth2TokenService;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@RequestParam("code") String code) {
        log.info("OAuth2 로그인 요청 - Authorization Code 길이: {}", code.length());

        // Authorization Code 유효성 검증
        if (code.trim().isEmpty()) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE, "Authorization code는 필수입니다.");
        }

        // OAuth2 토큰 교환 및 Customer 처리
        TokenResponse tokenResponse = oAuth2TokenService.exchangeCodeForToken(code);

        // Refresh Token을 HTTP-only, Secure 쿠키로 설정
        String refreshToken = jwtConfig.generateRefreshToken(tokenResponse.customerId().toString());
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .maxAge(Duration.ofDays(7)) // 7일
            .path("/")
            .build();

        // 응답 데이터 생성
        LoginResponse loginResponse = LoginResponse.of(
            tokenResponse.accessToken(),
            tokenResponse.customerId(),
            tokenResponse.expiresIn()
        );

        return ApiResult.ok(loginResponse)
            .cookie(refreshTokenCookie);
    }
}