package saviing.bank.auth.oauth2.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {

    private final Long userId;
    private final String name;
    private final String accessToken;
    private final Long expiresIn;

    public static TokenResponse of(Long userId, String name, String accessToken, Long expiresIn) {
        return TokenResponse.builder()
            .userId(userId)
            .name(name)
            .accessToken(accessToken)
            .expiresIn(expiresIn)
            .build();
    }
}