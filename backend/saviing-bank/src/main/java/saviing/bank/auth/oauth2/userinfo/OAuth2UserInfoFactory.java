package saviing.bank.auth.oauth2.userinfo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import saviing.bank.auth.oauth2.exception.OAuth2ErrorCode;
import saviing.bank.common.enums.OAuth2Provider;
import saviing.common.exception.BusinessException;

import java.util.Map;

@Slf4j
@Component
public class OAuth2UserInfoFactory {

    public OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        try {
            OAuth2Provider provider = extractProvider(registrationId);

            switch (provider) {
                case GOOGLE:
                    return createGoogleUserInfo(attributes);
                case KAKAO:
                    throw new BusinessException(OAuth2ErrorCode.UNSUPPORTED_PROVIDER, "KAKAO 로그인은 현재 지원하지 않습니다.");
                case NAVER:
                    throw new BusinessException(OAuth2ErrorCode.UNSUPPORTED_PROVIDER, "NAVER 로그인은 현재 지원하지 않습니다.");
                default:
                    throw new BusinessException(OAuth2ErrorCode.UNSUPPORTED_PROVIDER, "지원하지 않는 OAuth2 제공업체입니다: " + registrationId);
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("OAuth2 사용자 정보 생성 실패 - RegistrationId: {}", registrationId, ex);
            throw new BusinessException(OAuth2ErrorCode.UNSUPPORTED_PROVIDER, "OAuth2 사용자 정보 생성 중 오류가 발생했습니다.", ex);
        }
    }

    public OAuth2Provider getOAuth2Provider(String registrationId) {
        return extractProvider(registrationId);
    }

    private OAuth2UserInfo createGoogleUserInfo(Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            throw new BusinessException(OAuth2ErrorCode.USER_INFO_RETRIEVAL_FAILED, "Google OAuth2 사용자 정보가 비어있습니다.");
        }
        return new GoogleOAuth2UserInfo(attributes);
    }

    private OAuth2Provider extractProvider(String registrationId) {
        if (registrationId == null || registrationId.trim().isEmpty()) {
            throw new BusinessException(OAuth2ErrorCode.UNSUPPORTED_PROVIDER, "OAuth2 제공업체 정보가 없습니다.");
        }

        String lowerRegistrationId = registrationId.toLowerCase();

        if (lowerRegistrationId.startsWith("google")) {
            return OAuth2Provider.GOOGLE;
        } else if (lowerRegistrationId.startsWith("kakao")) {
            return OAuth2Provider.KAKAO;
        } else if (lowerRegistrationId.startsWith("naver")) {
            return OAuth2Provider.NAVER;
        }

        throw new BusinessException(OAuth2ErrorCode.UNSUPPORTED_PROVIDER, "지원하지 않는 OAuth2 제공업체입니다: " + registrationId);
    }
}