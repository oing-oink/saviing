package saviing.game.item.application.dto.enums;

/**
 * 코인 타입 열거형
 * 가격 정렬시 사용되는 코인 종류를 나타냅니다.
 */
public enum CoinType {
    COIN("coin"),
    FISH_COIN("fishCoin");

    private final String value;

    CoinType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}