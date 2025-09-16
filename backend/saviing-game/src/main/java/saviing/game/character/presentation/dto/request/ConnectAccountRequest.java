package saviing.game.character.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "계좌 연결 요청")
public record ConnectAccountRequest(
    @Schema(description = "계좌 ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "계좌 ID는 필수입니다")
    @Positive(message = "계좌 ID는 양수여야 합니다")
    Long accountId
) {
}