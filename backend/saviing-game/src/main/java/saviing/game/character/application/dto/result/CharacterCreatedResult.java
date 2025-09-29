package saviing.game.character.application.dto.result;

import lombok.Builder;
import saviing.game.character.domain.model.enums.ConnectionStatus;

import java.time.LocalDateTime;

/**
 * 캐릭터 생성 결과 Result입니다.
 *
 * @param characterId 생성된 캐릭터 ID
 * @param customerId 고객 ID
 * @param roomId 생성된 기본 방 ID (1번 방)
 * @param coin 보유 코인 수량 (기본값: 0)
 * @param fishCoin 보유 피쉬 코인 수량 (기본값: 0)
 * @param roomCount 보유 방 개수 (기본값: 1)
 * @param isActive 캐릭터 활성 상태 (기본값: true)
 * @param connectionStatus 계좌 연결 상태 (기본값: NO_ACCOUNT)
 * @param createdAt 캐릭터 생성 일시
 * @param updatedAt 캐릭터 수정 일시
 */
@Builder
public record CharacterCreatedResult(
    Long characterId,
    Long customerId,
    Long roomId,
    Integer coin,
    Integer fishCoin,
    Integer roomCount,
    Boolean isActive,
    ConnectionStatus connectionStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}