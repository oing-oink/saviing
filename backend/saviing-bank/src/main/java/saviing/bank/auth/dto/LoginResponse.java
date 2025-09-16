package saviing.bank.auth.dto;

/**
 * 로그인 API 응답 DTO
 * /v1/auth/login 엔드포인트에서 반환되는 로그인 성공 응답 데이터
 */
public record LoginResponse(
    String accessToken,
    Long customerId,
    Long expiresIn
) {
    /**
     * LoginResponse 생성을 위한 팩토리 메서드
     */
    public static LoginResponse of(String accessToken, Long customerId, Long expiresIn) {
        return new LoginResponse(accessToken, customerId, expiresIn);
    }
}