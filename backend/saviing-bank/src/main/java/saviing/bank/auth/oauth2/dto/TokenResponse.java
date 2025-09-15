package saviing.bank.auth.oauth2.dto;

/**
 * OAuth2 토큰 응답 DTO
 * 인증 성공 후 클라이언트에게 반환되는 토큰 정보를 담습니다.
 */
public record TokenResponse(
    Long customerId,
    String name,
    String accessToken,
    Long expiresIn
) {
    /**
     * TokenResponse 생성을 위한 팩토리 메서드
     */
    public static TokenResponse of(Long customerId, String name, String accessToken, Long expiresIn) {
        return new TokenResponse(customerId, name, accessToken, expiresIn);
    }
}