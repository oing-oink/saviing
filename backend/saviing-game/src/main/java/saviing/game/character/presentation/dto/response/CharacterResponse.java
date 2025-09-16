package saviing.game.character.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import saviing.game.character.domain.model.enums.ConnectionStatus;

import java.time.LocalDateTime;

@Builder
@Schema(description = "캐릭터 상세 정보 응답")
public record CharacterResponse(
    @Schema(description = "캐릭터 ID", example = "1")
    Long characterId,
    
    @Schema(description = "고객 ID", example = "123")
    Long customerId,
    
    @Schema(description = "연결된 계좌 ID (연결된 경우에만)", example = "456")
    Long accountId,
    
    @Schema(description = "계좌 연결 상태", example = "CONNECTED")
    ConnectionStatus connectionStatus,
    
    @Schema(description = "계좌 연결 일시 (연결된 경우에만)", example = "2024-01-01T10:00:00")
    LocalDateTime connectionDate,
    
    @Schema(description = "계좌 해지 사유 (해지된 경우에만)", example = "고객 요청")
    String terminationReason,
    
    @Schema(description = "계좌 해지 일시 (해지된 경우에만)", example = "2024-01-02T15:30:00")
    LocalDateTime terminatedAt,
    
    @Schema(description = "보유 코인 수량", example = "1000")
    Integer coin,
    
    @Schema(description = "보유 피쉬 코인 수량", example = "500")
    Integer fishCoin,
    
    @Schema(description = "보유 방 개수", example = "3")
    Integer roomCount,
    
    @Schema(description = "캐릭터 활성 상태", example = "true")
    Boolean isActive,
    
    @Schema(description = "캐릭터 비활성화 일시 (비활성화된 경우에만)", example = "2024-01-03T09:00:00")
    LocalDateTime deactivatedAt,
    
    @Schema(description = "캐릭터 생성 일시", example = "2024-01-01T09:00:00")
    LocalDateTime createdAt,
    
    @Schema(description = "캐릭터 정보 마지막 수정 일시", example = "2024-01-01T10:00:00")
    LocalDateTime updatedAt
) {
}