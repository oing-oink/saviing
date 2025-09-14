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
import saviing.bank.customer.entity.Customer;
import saviing.bank.customer.repository.CustomerRepository;
import saviing.common.config.JwtConfig;

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
     * OAuth2 Authorization Code를 처리하여 Customer를 생성/조회하고 JWT 토큰 발급
     */
    @Transactional
    public Map<String, Object> exchangeCodeForToken(String authorizationCode) {
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
     * Authorization Code를 Google Access Token으로 교환
     */
    private String exchangeCodeForAccessToken(String authorizationCode) {
        log.debug("Google Authorization Code를 Access Token으로 교환 시작");

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
            .orElseThrow(() -> {
                log.error("Google 토큰 교환 실패");
                return new IllegalArgumentException("Google 토큰 교환에 실패했습니다.");
            });
    }

    /**
     * Google Access Token으로 사용자 정보 조회
     */
    private OAuth2UserInfo getUserInfo(String accessToken) {
        log.debug("Google Access Token으로 사용자 정보 조회 시작");

        Map<String, Object> userAttributes = Optional.ofNullable(
                webClient.get()
                    .uri(googleUserInfoUrl)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block()
            )
            .orElseThrow(() -> {
                log.error("Google 사용자 정보 조회 실패");
                return new IllegalArgumentException("Google 사용자 정보 조회에 실패했습니다.");
            });

        OAuth2UserInfo userInfo = oAuth2UserInfoFactory.getOAuth2UserInfo("google", userAttributes);
        log.debug("Google 사용자 정보 조회 성공 - ID: {}", userInfo.getId());

        return userInfo;
    }

    /**
     * Customer 생성 또는 조회
     */
    private Customer processCustomer(OAuth2UserInfo userInfo) {
        Customer.OAuth2Provider provider = Customer.OAuth2Provider.GOOGLE;
        String oauth2Id = userInfo.getId();

        return customerRepository.findByOauth2ProviderAndOauth2Id(provider, oauth2Id)
            .map(existingCustomer -> {
                log.info("기존 고객 로그인 - CustomerId: {}, Name: {}",
                    existingCustomer.getCustomerId(), existingCustomer.getName());
                return existingCustomer;
            })
            .orElseGet(() -> {
                Customer newCustomer = Customer.builder()
                    .name(userInfo.getName())
                    .oauth2Provider(provider)
                    .oauth2Id(oauth2Id)
                    .build();

                Customer savedCustomer = customerRepository.save(newCustomer);
                log.info("신규 고객 자동 가입 - CustomerId: {}, Name: {}",
                    savedCustomer.getCustomerId(), savedCustomer.getName());
                return savedCustomer;
            });
    }

    /**
     * JWT 토큰 응답 생성
     * Customer 정보를 기반으로 JWT Access Token을 생성하여 응답 객체를 구성합니다.
     *
     * @param customer 인증된 Customer 엔티티
     * @return 토큰 응답 Map
     */
    private Map<String, Object> createTokenResponse(Customer customer) {
        // JWT Access Token 생성 (userId, name, expiresIn 포함)
        String accessToken = jwtConfig.generateAccessToken(
            customer.getCustomerId().toString(),
            Map.of(
                "userId", customer.getCustomerId(),
                "name", customer.getName(),
                "expiresIn", jwtConfig.getTokenExpiryInSeconds()
            )
        );

        // Refresh Token 생성 (쿠키로 설정 예정)
        String refreshToken = jwtConfig.generateRefreshToken(customer.getCustomerId().toString());

        // 응답 데이터 구성
        return Map.of(
            "userId", customer.getCustomerId(),
            "name", customer.getName(),
            "accessToken", accessToken,
            "expiresIn", jwtConfig.getTokenExpiryInSeconds()
        );
    }
}