package saviing.game.pet.application.dto.result;

import lombok.Builder;

import java.util.List;

/**
 * 펫 상호작용 결과 DTO
 * 상호작용 후 펫 정보와 소모된 소모품 정보를 포함합니다.
 */
@Builder
public record PetInteractionResult(
    PetResult pet,
    List<ConsumptionResult> consumption
) {
}