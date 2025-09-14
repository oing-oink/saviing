package saviing.bank.auth.oauth2.userinfo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import saviing.bank.customer.entity.Customer;

import java.util.Map;

@Slf4j
@Component
public class OAuth2UserInfoFactory {

    public OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        try {
            Customer.OAuth2Provider provider = extractProvider(registrationId);

            log.debug("OAuth2 사용자 정보 생성 - Provider: {}, RegistrationId: {}", provider, registrationId);

            switch (provider) {
                case GOOGLE:
                    return createGoogleUserInfo(attributes);
                case KAKAO:
                    throw new IllegalArgumentException("KAKAO 로그인은 현재 지원하지 않습니다.");
                case NAVER:
                    throw new IllegalArgumentException("NAVER 로그인은 현재 지원하지 않습니다.");
                default:
                    throw new IllegalArgumentException("지원하지 않는 OAuth2 제공업체입니다: " + registrationId);
            }
        } catch (Exception ex) {
            log.error("OAuth2 사용자 정보 생성 실패 - RegistrationId: {}", registrationId, ex);
            throw ex;
        }
    }

    public Customer.OAuth2Provider getOAuth2Provider(String registrationId) {
        return extractProvider(registrationId);
    }

    private OAuth2UserInfo createGoogleUserInfo(Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            throw new IllegalArgumentException("Google OAuth2 사용자 정보가 비어있습니다.");
        }
        return new GoogleOAuth2UserInfo(attributes);
    }

    private Customer.OAuth2Provider extractProvider(String registrationId) {
        if (registrationId == null || registrationId.trim().isEmpty()) {
            throw new IllegalArgumentException("OAuth2 제공업체 정보가 없습니다.");
        }

        String lowerRegistrationId = registrationId.toLowerCase();

        if (lowerRegistrationId.startsWith("google")) {
            return Customer.OAuth2Provider.GOOGLE;
        } else if (lowerRegistrationId.startsWith("kakao")) {
            return Customer.OAuth2Provider.KAKAO;
        } else if (lowerRegistrationId.startsWith("naver")) {
            return Customer.OAuth2Provider.NAVER;
        }

        throw new IllegalArgumentException("지원하지 않는 OAuth2 제공업체입니다: " + registrationId);
    }
}