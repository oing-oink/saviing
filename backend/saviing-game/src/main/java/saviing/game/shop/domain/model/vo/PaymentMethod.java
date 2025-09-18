package saviing.game.shop.domain.model.vo;

/**
 * 결제 수단을 나타내는 열거형입니다.
 */
public enum PaymentMethod {
    /**
     * 일반 코인으로 결제
     */
    COIN,

    /**
     * 피쉬 코인으로 결제
     */
    FISH_COIN;

    /**
     * 일반 코인 결제인지 확인합니다.
     *
     * @return 일반 코인 결제 여부
     */
    public boolean isCoin() {
        return this == COIN;
    }

    /**
     * 피쉬 코인 결제인지 확인합니다.
     *
     * @return 피쉬 코인 결제 여부
     */
    public boolean isFishCoin() {
        return this == FISH_COIN;
    }

    /**
     * 통화 문자열을 반환합니다.
     *
     * @return 통화 문자열
     */
    public String getCurrency() {
        return switch (this) {
            case COIN -> "COIN";
            case FISH_COIN -> "FISH_COIN";
        };
    }
}