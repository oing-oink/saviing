package saviing.game.character.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "캐릭터 생성 요청")
public record CreateCharacterRequest(
    @Schema(description = "고객 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "고객 ID는 필수입니다")
    @Positive(message = "고객 ID는 양수여야 합니다")
    Long customerId
) {
}