package saviing.game.pet.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 펫 이름 변경 요청 DTO
 */
public record ChangePetNameRequest(
    @NotNull(message = "펫 이름은 필수입니다")
    @Size(min = 1, max = 20, message = "펫 이름은 1자 이상 20자 이하여야 합니다")
    @JsonProperty("name")
    String name
) {
}