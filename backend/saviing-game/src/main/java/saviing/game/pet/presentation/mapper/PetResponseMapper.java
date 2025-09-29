package saviing.game.pet.presentation.mapper;

import org.springframework.stereotype.Component;
import saviing.game.pet.application.dto.result.ConsumptionResult;
import saviing.game.pet.application.dto.result.PetResult;
import saviing.game.pet.presentation.dto.response.ConsumptionResponse;
import saviing.game.pet.presentation.dto.response.PetInfoResponse;
import saviing.game.pet.presentation.dto.response.PetInteractionResponse;

import java.util.List;

/**
 * PetResult를 PetInfoResponse로 변환하는 매퍼
 */
@Component
public class PetResponseMapper {

    private static final int MAX_AFFECTION = 100;
    private static final int MAX_ENERGY = 100;

    /**
     * PetResult를 PetInfoResponse로 변환합니다.
     *
     * @param result PetResult
     * @return PetInfoResponse
     */
    public PetInfoResponse toResponse(PetResult result) {
        if (result == null) {
            return null;
        }

        return PetInfoResponse.builder()
            .petId(result.petId())
            .itemId(result.itemId())
            .name(result.petName())
            .level(result.level())
            .exp(result.experience())
            .requiredExp(result.requiredExp())
            .affection(result.affection())
            .maxAffection(MAX_AFFECTION)
            .energy(result.energy())
            .maxEnergy(MAX_ENERGY)
            .build();
    }

    /**
     * PetResult와 ConsumptionResult 리스트를 PetInteractionResponse로 변환합니다.
     *
     * @param petResult PetResult
     * @param consumptionResults ConsumptionResult 리스트
     * @return PetInteractionResponse
     */
    public PetInteractionResponse toInteractionResponse(PetResult petResult, List<ConsumptionResult> consumptionResults) {
        PetInfoResponse petInfo = toResponse(petResult);
        List<ConsumptionResponse> consumption = consumptionResults.stream()
                .map(this::toConsumptionResponse)
                .toList();

        return new PetInteractionResponse(petInfo, consumption);
    }

    /**
     * ConsumptionResult를 ConsumptionResponse로 변환합니다.
     *
     * @param result ConsumptionResult
     * @return ConsumptionResponse
     */
    private ConsumptionResponse toConsumptionResponse(ConsumptionResult result) {
        return new ConsumptionResponse(
                result.inventoryItemId(),
                result.itemId(),
                result.type(),
                result.remaining()
        );
    }

}