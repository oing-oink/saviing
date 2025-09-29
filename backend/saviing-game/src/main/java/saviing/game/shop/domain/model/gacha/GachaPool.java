package saviing.game.shop.domain.model.gacha;

import saviing.game.item.domain.model.enums.Rarity;
import saviing.game.item.domain.model.vo.Price;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 통합된 가챠 풀 구성을 정의한 enum입니다.
 * 가챠 풀 정보와 보상 확률을 함께 관리합니다.
 */
public enum GachaPool {
    DEFAULT(1L, "뽑기", Price.coinOnly(500), createDefaultDropRates());

    private final long gachaPoolId;
    private final String gachaPoolName;
    private final Price price;
    private final Map<Rarity, Integer> dropRates;

    GachaPool(long gachaPoolId, String gachaPoolName, Price price, Map<Rarity, Integer> dropRates) {
        this.gachaPoolId = gachaPoolId;
        this.gachaPoolName = gachaPoolName;
        this.price = price;
        this.dropRates = Collections.unmodifiableMap(dropRates);
    }

    public long id() {
        return gachaPoolId;
    }

    public String poolName() {
        return gachaPoolName;
    }

    public Price price() {
        return price;
    }

    public Map<Rarity, Integer> dropRates() {
        return dropRates;
    }

    /**
     * 확률에 따라 희귀도를 뽑습니다.
     *
     * @return 뽑힌 희귀도
     */
    public Rarity drawRarity() {
        int randomValue = ThreadLocalRandom.current().nextInt(100) + 1;
        int cumulative = 0;

        for (Map.Entry<Rarity, Integer> entry : dropRates.entrySet()) {
            cumulative += entry.getValue();
            if (randomValue <= cumulative) {
                return entry.getKey();
            }
        }

        // 기본값으로 COMMON 반환
        return Rarity.COMMON;
    }

    /**
     * 기본 가챠 풀의 드롭 확률을 생성합니다.
     */
    private static Map<Rarity, Integer> createDefaultDropRates() {
        Map<Rarity, Integer> rates = new EnumMap<>(Rarity.class);
        rates.put(Rarity.COMMON, 70);
        rates.put(Rarity.RARE, 20);
        rates.put(Rarity.EPIC, 9);
        rates.put(Rarity.LEGENDARY, 1);
        return rates;
    }
}