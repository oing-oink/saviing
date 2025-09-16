package saviing.game.character.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "계좌 해지 이벤트 처리 요청 (내부 API용)")
public record HandleAccountTerminatedRequest(
    @Schema(description = "계좌 해지 사유", example = "고객 요청", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "해지 사유는 필수입니다")
    String terminationReason
) {
}