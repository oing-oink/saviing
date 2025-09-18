package saviing.game.item.domain.model.vo;

/**
 * 아이템 가격 Value Object
 * 일반 코인과 피쉬 코인 가격을 관리합니다.
 */
public record Price(
    Integer coin,
    Integer fishCoin
) {
    public Price {
        if (coin == null && fishCoin == null) {
            throw new IllegalArgumentException("코인 또는 피쉬 코인 중 하나 이상의 가격이 설정되어야 합니다");
        }
        if (coin != null && coin < 0) {
            throw new IllegalArgumentException("코인 가격은 0 이상이어야 합니다");
        }
        if (fishCoin != null && fishCoin < 0) {
            throw new IllegalArgumentException("피쉬 코인 가격은 0 이상이어야 합니다");
        }
    }

    /**
     * 코인과 피쉬 코인 가격으로 Price를 생성합니다.
     *
     * @param coin 코인 가격
     * @param fishCoin 피쉬 코인 가격
     * @return Price 인스턴스
     */
    public static Price of(Integer coin, Integer fishCoin) {
        return new Price(coin, fishCoin);
    }

    /**
     * 코인만으로 Price를 생성합니다.
     *
     * @param coin 코인 가격
     * @return Price 인스턴스
     */
    public static Price coinOnly(Integer coin) {
        return new Price(coin, null);
    }

    /**
     * 피쉬 코인만으로 Price를 생성합니다.
     *
     * @param fishCoin 피쉬 코인 가격
     * @return Price 인스턴스
     */
    public static Price fishCoinOnly(Integer fishCoin) {
        return new Price(null, fishCoin);
    }

    /**
     * 무료 가격을 생성합니다.
     *
     * @return 무료 Price 인스턴스
     */
    public static Price free() {
        return new Price(0, 0);
    }

    /**
     * 코인 가격이 설정되어 있는지 확인합니다.
     *
     * @return 코인 가격 설정 여부
     */
    public boolean hasCoinPrice() {
        return coin != null && coin > 0;
    }

    /**
     * 피쉬 코인 가격이 설정되어 있는지 확인합니다.
     *
     * @return 피쉬 코인 가격 설정 여부
     */
    public boolean hasFishCoinPrice() {
        return fishCoin != null && fishCoin > 0;
    }

    /**
     * 무료인지 확인합니다.
     *
     * @return 무료인지 여부
     */
    public boolean isFree() {
        return (coin == null || coin == 0) && (fishCoin == null || fishCoin == 0);
    }

    /**
     * 두 가지 통화 모두 가격이 설정되어 있는지 확인합니다.
     *
     * @return 두 통화 모두 가격이 설정되어 있는지 여부
     */
    public boolean hasBothCurrencies() {
        return hasCoinPrice() && hasFishCoinPrice();
    }
}