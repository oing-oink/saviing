package saviing.bank.auth.oauth2.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {

    private final Long customerId;
    private final String name;
    private final String accessToken;
    private final Long expiresIn;

    public static TokenResponse of(Long customerId, String name, String accessToken, Long expiresIn) {
        return TokenResponse.builder()
            .customerId(customerId)
            .name(name)
            .accessToken(accessToken)
            .expiresIn(expiresIn)
            .build();
    }
}