package saviing.game.character.domain.model.vo;

import java.time.LocalDateTime;

/**
 * 계좌 해지 정보를 관리하는 Value Object
 * 해지 사유와 해지 시간을 포함합니다.
 */
public record AccountTermination(
    String reason,
    LocalDateTime terminatedAt
) {
    public AccountTermination {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("해지 사유는 비어있을 수 없습니다");
        }
        if (terminatedAt == null) {
            throw new IllegalArgumentException("해지 일시는 null일 수 없습니다");
        }
    }

    /**
     * 해지 정보를 생성합니다.
     * 
     * @param reason 해지 사유
     * @return 해지 정보
     */
    public static AccountTermination of(String reason) {
        return new AccountTermination(reason, LocalDateTime.now());
    }
}