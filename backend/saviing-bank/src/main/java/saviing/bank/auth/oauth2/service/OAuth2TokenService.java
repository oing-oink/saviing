package saviing.bank.auth.oauth2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import saviing.bank.auth.oauth2.userinfo.OAuth2UserInfo;
import saviing.bank.auth.oauth2.userinfo.OAuth2UserInfoFactory;
import saviing.bank.auth.oauth2.exception.OAuth2ErrorCode;
import saviing.bank.auth.exception.AuthErrorCode;
import saviing.bank.auth.dto.LoginResponse;
import saviing.bank.auth.dto.RefreshResponse;
import saviing.bank.auth.oauth2.dto.TokenResponse;
import saviing.bank.common.enums.OAuth2Provider;
import saviing.bank.customer.entity.Customer;
import saviing.bank.customer.repository.CustomerRepository;
import saviing.common.config.JwtConfig;
import saviing.common.exception.BusinessException;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2TokenService {

    private final WebClient webClient;
    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;
    private final CustomerRepository customerRepository;
    private final JwtConfig jwtConfig;

    @Value("${oauth2.google.client-id}")
    private String googleClientId;

    @Value("${oauth2.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth2.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${oauth2.google.token-url:https://oauth2.googleapis.com/token}")
    private String googleTokenUrl;

    @Value("${oauth2.google.user-info-url:https://www.googleapis.com/oauth2/v3/userinfo}")
    private String googleUserInfoUrl;

    /**
     * OAuth2 로그인 처리 - Access Token과 Refresh Token 쿠키를 포함한 완전한 로그인 응답 생성
     */
    @Transactional
    public LoginResponseWithCookie processLogin(String authorizationCode) {
        // 1. Authorization Code → Google Access Token
        String googleAccessToken = exchangeCodeForAccessToken(authorizationCode);

        // 2. Google Access Token → 사용자 정보
        OAuth2UserInfo userInfo = getUserInfo(googleAccessToken);

        // 3. Customer 생성 또는 조회
        Customer customer = processCustomer(userInfo);

        // 4. JWT Access Token 생성
        String accessToken = createAccessToken(customer);

        // 5. Refresh Token 생성 및 쿠키 설정
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(customer);

        // 6. 로그인 응답 생성
        LoginResponse loginResponse = LoginResponse.of(
            accessToken,
            customer.getCustomerId(),
            jwtConfig.getTokenExpiryInSeconds()
        );

        return new LoginResponseWithCookie(loginResponse, refreshTokenCookie);
    }

    /**
     * OAuth2 Authorization Code를 처리하여 Customer를 생성/조회하고 JWT 토큰 발급
     */
    @Transactional
    public TokenResponse exchangeCodeForToken(String authorizationCode) {
        // 1. Authorization Code → Google Access Token
        String googleAccessToken = exchangeCodeForAccessToken(authorizationCode);

        // 2. Google Access Token → 사용자 정보
        OAuth2UserInfo userInfo = getUserInfo(googleAccessToken);

        // 3. Customer 생성 또는 조회
        Customer customer = processCustomer(userInfo);

        // 4. JWT 토큰 응답 생성
        return createTokenResponse(customer);
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token과 Refresh Token 발급
     */
    public RefreshResponseWithCookie refreshTokens(String refreshToken) {
        // 1. Refresh Token 유효성 검증
        if (!jwtConfig.isTokenValid(refreshToken) || !jwtConfig.isRefreshToken(refreshToken)) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. Refresh Token에서 customerId 추출
        String customerId = jwtConfig.getSubjectFromToken(refreshToken);

        // 3. Customer 조회
        Customer customer = customerRepository.findById(Long.valueOf(customerId))
            .orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));

        // 4. 새로운 Access Token 생성
        String newAccessToken = createAccessToken(customer);

        // 5. 새로운 Refresh Token 생성 및 쿠키 설정
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(customer);

        // 6. 응답 생성 (RefreshResponse와 쿠키)
        RefreshResponse refreshResponse = RefreshResponse.of(
            newAccessToken,
            jwtConfig.getTokenExpiryInSeconds()
        );

        return new RefreshResponseWithCookie(refreshResponse, refreshTokenCookie);
    }

    /**
     * 로그아웃 처리 - Refresh Token 검증 및 쿠키 삭제
     */
    public ResponseCookie logout(String refreshToken) {
        // 1. Refresh Token 유효성 검증 (JwtConfig에서 처리)
        if (!jwtConfig.isTokenValid(refreshToken) || !jwtConfig.isRefreshToken(refreshToken)) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. Refresh Token에서 customerId 추출
        String customerId = jwtConfig.getSubjectFromToken(refreshToken);

        // 3. 로그 기록
        log.info("사용자 {} 로그아웃 처리 - Refresh Token 쿠키 삭제", customerId);

        // 4. 쿠키 삭제 (JwtConfig에서 처리)
        return jwtConfig.createExpiredRefreshTokenCookie();
    }

    /**
     * Authorization Code를 Google Access Token으로 교환
     */
    private String exchangeCodeForAccessToken(String authorizationCode) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("code", authorizationCode);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", googleRedirectUri);

        return Optional.ofNullable(
            webClient.post()
                .uri(googleTokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .bodyToMono(Map.class)
                .block()
            )
            .filter(response -> response.containsKey("access_token"))
            .map(response -> (String) response.get("access_token"))
            .orElseThrow(() -> new BusinessException(OAuth2ErrorCode.TOKEN_EXCHANGE_FAILED));
    }

    /**
     * Google Access Token으로 사용자 정보 조회
     */
    private OAuth2UserInfo getUserInfo(String accessToken) {

        Map<String, Object> userAttributes = Optional.ofNullable(
                webClient.get()
                    .uri(googleUserInfoUrl)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block()
            )
            .orElseThrow(() -> new BusinessException(OAuth2ErrorCode.USER_INFO_RETRIEVAL_FAILED));

        return oAuth2UserInfoFactory.getOAuth2UserInfo("google", userAttributes);
    }

    /**
     * Customer 생성 또는 조회
     */
    private Customer processCustomer(OAuth2UserInfo userInfo) {
        OAuth2Provider provider = OAuth2Provider.GOOGLE;
        String oauth2Id = userInfo.getId();

        return customerRepository.findByOauth2ProviderAndOauth2Id(provider, oauth2Id)
            .orElseGet(() -> {
                Customer newCustomer = Customer.builder()
                    .name(userInfo.getName())
                    .oauth2Provider(provider)
                    .oauth2Id(oauth2Id)
                    .build();

                return customerRepository.save(newCustomer);
            });
    }

    /**
     * Customer 정보를 기반으로 JWT Access Token 생성
     */
    private String createAccessToken(Customer customer) {
        return jwtConfig.generateAccessToken(
            customer.getCustomerId().toString(),
            Map.of(
                "customerId", customer.getCustomerId(),
                "name", customer.getName(),
                "expiresIn", jwtConfig.getTokenExpiryInSeconds()
            )
        );
    }

    /**
     * Customer 정보를 기반으로 Refresh Token 쿠키 생성
     */
    private ResponseCookie createRefreshTokenCookie(Customer customer) {
        String refreshToken = jwtConfig.generateRefreshToken(customer.getCustomerId().toString());
        return jwtConfig.createRefreshTokenCookie(refreshToken);
    }

    /**
     * JWT 토큰 응답 생성
     * Customer 정보를 기반으로 JWT Access Token을 생성하여 응답 객체를 구성합니다.
     *
     * @param customer 인증된 Customer 엔티티
     * @return 토큰 응답 DTO
     */
    private TokenResponse createTokenResponse(Customer customer) {
        // JWT Access Token 생성 (customerId, name, expiresIn 포함)
        String accessToken = createAccessToken(customer);

        // 응답 데이터 구성
        return TokenResponse.of(
            customer.getCustomerId(),
            customer.getName(),
            accessToken,
            jwtConfig.getTokenExpiryInSeconds()
        );
    }
}