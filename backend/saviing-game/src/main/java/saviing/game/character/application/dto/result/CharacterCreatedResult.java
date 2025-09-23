package saviing.game.character.application.dto.result;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 캐릭터 생성 결과 Result입니다.
 *
 * @param characterId 생성된 캐릭터 ID
 * @param customerId 고객 ID
 * @param roomId 생성된 기본 방 ID (1번 방)
 * @param createdAt 캐릭터 생성 일시
 */
@Builder
public record CharacterCreatedResult(
    Long characterId,
    Long customerId,
    Long roomId,
    LocalDateTime createdAt
) {
}