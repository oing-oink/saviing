package saviing.game.pet.presentation.dto.response;

import lombok.Builder;

/**
 * 펫 정보 API 응답 DTO
 */
@Builder
public record PetInfoResponse(
    Long petId,
    Long itemId,
    String name,
    Integer level,
    Integer exp,
    Integer requiredExp,
    Integer affection,
    Integer maxAffection,
    Integer energy,
    Integer maxEnergy
) {
}