package saviing.game.pet.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import saviing.game.item.domain.model.enums.Consumption;

/**
 * 소모품 응답 DTO
 */
public record ConsumptionResponse(
    @JsonProperty("inventoryItemId")
    Long inventoryItemId,

    @JsonProperty("itemId")
    Long itemId,

    @JsonProperty("type")
    Consumption type,

    @JsonProperty("remaining")
    int remaining
) {
}