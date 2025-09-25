package saviing.game.pet.application.dto.result;

import lombok.Builder;
import saviing.game.item.domain.model.enums.Consumption;

/**
 * 소모품 정보 결과 DTO
 * 상호작용 후 소모된 소모품 정보를 담습니다.
 */
@Builder
public record ConsumptionResult(
    Long inventoryItemId,
    Long itemId,
    Consumption type,
    int remaining
) {
}