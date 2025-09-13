package saviing.game.character.presentation.dto.request;

/**
 * 코인 추가 요청 DTO입니다.
 *
 * @param coinAmount 추가할 코인 수량
 * @param fishCoinAmount 추가할 피쉬 코인 수량
 */
public record AddCoinsRequest(
    Integer coinAmount,
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