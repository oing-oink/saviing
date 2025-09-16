package saviing.bank.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import saviing.bank.auth.dto.LoginResponse;
import saviing.bank.auth.oauth2.service.OAuth2TokenService;
import saviing.bank.auth.oauth2.service.LoginResponseWithCookie;
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
}