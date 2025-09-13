package saviing.game.character.presentation.dto.response;

import lombok.Builder;
import saviing.game.character.domain.model.enums.ConnectionStatus;

import java.time.LocalDateTime;

/**
 * 캐릭터 상세 정보 Response
 */
@Builder
public record CharacterResponse(
    Long characterId,
    Long customerId,
    Long accountId,
    ConnectionStatus connectionStatus,
    LocalDateTime connectionDate,
    String terminationReason,
    LocalDateTime terminatedAt,
    Integer coin,
    Integer fishCoin,
    Integer roomCount,
    Boolean isActive,
    LocalDateTime deactivatedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}