package saviing.game.character.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 계좌 연결 요청 DTO
 */
public record ConnectAccountRequest(
    @NotNull(message = "계좌 ID는 필수입니다")
    @Positive(message = "계좌 ID는 양수여야 합니다")
    Long accountId
) {
}