package saviing.game.character.application.dto.result;

import lombok.Builder;
import saviing.game.character.domain.model.enums.ConnectionStatus;

import java.time.LocalDateTime;

/**
 * 캐릭터 조회 결과 Result입니다.
 *
 * @param characterId 캐릭터 ID
 * @param customerId 고객 ID
 * @param accountId 연결된 계좌 ID
 * @param connectionStatus 계좌 연결 상태
 * @param connectionDate 계좌 연결 일시
 * @param terminationReason 계좌 해지 사유
 * @param terminatedAt 계좌 해지 일시
 * @param coin 보유 코인 수량
 * @param fishCoin 보유 피쉬 코인 수량
 * @param roomCount 보유 방 개수
 * @param isActive 캐릭터 활성 여부
 * @param deactivatedAt 캐릭터 비활성화 일시
 * @param createdAt 캐릭터 생성 일시
 * @param updatedAt 캐릭터 최종 수정 일시
 */
@Builder
public record CharacterResult(
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