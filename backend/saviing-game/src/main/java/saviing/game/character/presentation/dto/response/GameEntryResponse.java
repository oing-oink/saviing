package saviing.game.character.presentation.dto.response;

import lombok.Builder;

/**
 * 메인 엔트리 게임 정보 API 응답 DTO
 */
@Builder
public record GameEntryResponse(
    Long characterId,
    Long roomId,
    GameEntryPetInfo pet
) {
}