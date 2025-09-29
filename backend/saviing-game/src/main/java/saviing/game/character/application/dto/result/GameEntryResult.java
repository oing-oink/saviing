package saviing.game.character.application.dto.result;

import lombok.Builder;
import saviing.game.pet.application.dto.result.PetResult;

/**
 * 메인 엔트리 게임 정보 조회 결과
 */
@Builder
public record GameEntryResult(
    Long characterId,
    Long roomId,
    PetResult pet
) {
}