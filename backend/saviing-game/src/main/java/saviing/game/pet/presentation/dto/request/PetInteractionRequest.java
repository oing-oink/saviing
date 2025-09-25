package saviing.game.pet.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import saviing.game.pet.domain.model.enums.InteractionType;

/**
 * 펫 상호작용 요청 DTO
 */
public record PetInteractionRequest(
    @NotNull(message = "상호작용 타입은 필수입니다")
    @JsonProperty("type")
    InteractionType type
) {
}