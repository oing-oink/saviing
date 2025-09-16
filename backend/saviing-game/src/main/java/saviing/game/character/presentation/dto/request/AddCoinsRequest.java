package saviing.game.character.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "코인 지급 요청 (내부 API용)")
public record AddCoinsRequest(
    @Schema(description = "지급할 코인 수량 (선택사항)", example = "100", minimum = "1")
    Integer coinAmount,
    
    @Schema(description = "지급할 피쉬 코인 수량 (선택사항)", example = "50", minimum = "1")
    Integer fishCoinAmount
) {
    public AddCoinsRequest {
        if (coinAmount != null && coinAmount <= 0) {
            throw new IllegalArgumentException("코인 수량은 양수여야 합니다");
        }
        if (fishCoinAmount != null && fishCoinAmount <= 0) {
            throw new IllegalArgumentException("피쉬 코인 수량은 양수여야 합니다");
        }
        if ((coinAmount == null || coinAmount == 0) && (fishCoinAmount == null || fishCoinAmount == 0)) {
            throw new IllegalArgumentException("코인 또는 피쉬 코인 중 적어도 하나는 양수여야 합니다");
        }
    }
}