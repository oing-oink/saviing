package saviing.game.character.presentation.dto.response;

import lombok.Builder;

/**
 * 메인 엔트리 게임 정보 API의 펫 정보 DTO
 * Character 도메인 내에서 정의하여 Pet 도메인과의 결합도를 줄입니다.
 */
@Builder
public record GameEntryPetInfo(
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