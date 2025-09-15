package saviing.game.character.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 계좌 해지 처리 요청 DTO
 */
public record HandleAccountTerminatedRequest(
    @NotBlank(message = "해지 사유는 필수입니다")
    String terminationReason
) {
}