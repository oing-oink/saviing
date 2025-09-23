package saviing.game.pet.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 펫 상호작용 응답 DTO
 */
public record PetInteractionResponse(
    @JsonProperty("pet")
    PetInfoResponse pet,

    @JsonProperty("consumption")
    List<ConsumptionResponse> consumption
) {
}