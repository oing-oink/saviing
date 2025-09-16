package saviing.game.character.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "계좌 연결 완료 요청 (내부 API용)")
public record CompleteAccountConnectionRequest(
    @Schema(description = "연결 완료된 계좌 ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "계좌 ID는 필수입니다")
    @Positive(message = "계좌 ID는 양수여야 합니다")
    Long accountId
) {
}