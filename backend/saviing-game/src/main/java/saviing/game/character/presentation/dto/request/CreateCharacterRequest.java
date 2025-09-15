package saviing.game.character.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 캐릭터 생성 요청 DTO입니다.
 *
 * @param customerId 고객 ID
 */
public record CreateCharacterRequest(
    @NotNull(message = "고객 ID는 필수입니다")
    @Positive(message = "고객 ID는 양수여야 합니다")
    Long customerId
) {
}