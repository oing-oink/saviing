package saviing.bank.auth.dto;

/**
 * 토큰 갱신 API 응답 DTO
 * /v1/auth/refresh 엔드포인트에서 반환되는 토큰 갱신 성공 응답 데이터
 */
public record RefreshResponse(
    String accessToken,
    Long expiresIn
) {
    /**
     * RefreshResponse 생성을 위한 팩토리 메서드
     */
    public static RefreshResponse of(String accessToken, Long expiresIn) {
        return new RefreshResponse(accessToken, expiresIn);
    }
}