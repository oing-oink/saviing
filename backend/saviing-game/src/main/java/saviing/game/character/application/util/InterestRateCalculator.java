package saviing.game.character.application.util;

import lombok.extern.slf4j.Slf4j;
import saviing.game.character.application.dto.result.CharacterStatisticsResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * 캐릭터 통계 기반 이자율 계산 유틸리티 클래스.
 *
 * 게임 진행도(펫 레벨, 장식품 희귀도)에 따라 적금 계좌의 보너스 이자율을 계산합니다.
 * 계산 공식: 1.5% ~ 4.5% 범위에서 가중 평균으로 산출
 */
@Slf4j
public class InterestRateCalculator {

    /**
     * 기본 이자율 (최소값)
     */
    private static final double BASE_INTEREST_RATE = 1.5;

    /**
     * 이자율 범위 (최대값 - 최소값)
     */
    private static final double INTEREST_RATE_RANGE = 3.0; // 4.5 - 1.5

    /**
     * 펫 레벨 합계 최대값
     */
    private static final int MAX_PET_LEVEL_SUM = 100;

    /**
     * 카테고리별 희귀도 최대값
     */
    private static final int MAX_CATEGORY_RARITY = 20;

    /**
     * 펫 레벨 합계 가중치 (60%)
     */
    private static final double PET_LEVEL_WEIGHT = 0.6;

    /**
     * 카테고리별 가중치 (각 10%)
     */
    private static final double CATEGORY_WEIGHT = 0.1;

    /**
     * 캐릭터 통계를 기반으로 이자율을 계산합니다.
     *
     * 계산 공식:
     * - topPetLevelSum: 최대 100, 가중치 60%
     * - PET.CAT: 최대 20, 가중치 10%
     * - DECORATION.LEFT: 최대 20, 가중치 10%
     * - DECORATION.RIGHT: 최대 20, 가중치 10%
     * - DECORATION.BOTTOM: 최대 20, 가중치 10%
     *
     * 결과: 1.5% ~ 4.5% 범위의 이자율
     *
     * @param statistics 캐릭터 통계 결과
     * @return 계산된 보너스 이자율 (백분율, 예: 3.5)
     */
    public static BigDecimal calculateInterestRate(CharacterStatisticsResult statistics) {
        if (statistics == null) {
            log.warn("캐릭터 통계가 null입니다. 기본 이자율 반환: {}%", BASE_INTEREST_RATE);
            return BigDecimal.valueOf(BASE_INTEREST_RATE);
        }

        log.debug("이자율 계산 시작: characterId={}, topPetLevelSum={}",
            statistics.characterId(), statistics.topPetLevelSum());

        // 1. 펫 레벨 점수 계산 (최대 100, 가중치 60%)
        int petLevelSum = Math.min(statistics.topPetLevelSum() != null ? statistics.topPetLevelSum() : 0, MAX_PET_LEVEL_SUM);
        double petScore = (double) petLevelSum / MAX_PET_LEVEL_SUM * PET_LEVEL_WEIGHT;

        // 2. 카테고리별 희귀도 점수 계산 (각 최대 20, 가중치 10%)
        int catRarity = getCategoryRarity(statistics, "PET", "CAT");
        int leftRarity = getCategoryRarity(statistics, "DECORATION", "LEFT");
        int rightRarity = getCategoryRarity(statistics, "DECORATION", "RIGHT");
        int bottomRarity = getCategoryRarity(statistics, "DECORATION", "BOTTOM");

        double catScore = (double) catRarity / MAX_CATEGORY_RARITY * CATEGORY_WEIGHT;
        double leftScore = (double) leftRarity / MAX_CATEGORY_RARITY * CATEGORY_WEIGHT;
        double rightScore = (double) rightRarity / MAX_CATEGORY_RARITY * CATEGORY_WEIGHT;
        double bottomScore = (double) bottomRarity / MAX_CATEGORY_RARITY * CATEGORY_WEIGHT;

        // 3. 총 점수 계산 (0.0 ~ 1.0)
        double totalScore = petScore + catScore + leftScore + rightScore + bottomScore;

        // 4. 이자율 계산 (1.5% ~ 4.5%)
        double interestRate = BASE_INTEREST_RATE + (totalScore * INTEREST_RATE_RANGE);

        log.debug("이자율 계산 상세: characterId={}, petLevel={}/{} ({}%), cat={}/{} ({}%), " +
                "left={}/{} ({}%), right={}/{} ({}%), bottom={}/{} ({}%), " +
                "totalScore={}, finalRate={}%",
            statistics.characterId(), petLevelSum, MAX_PET_LEVEL_SUM, petScore * 100,
            catRarity, MAX_CATEGORY_RARITY, catScore * 100,
            leftRarity, MAX_CATEGORY_RARITY, leftScore * 100,
            rightRarity, MAX_CATEGORY_RARITY, rightScore * 100,
            bottomRarity, MAX_CATEGORY_RARITY, bottomScore * 100,
            totalScore, interestRate);

        return BigDecimal.valueOf(interestRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 특정 ItemType과 카테고리의 희귀도 값을 조회합니다.
     *
     * @param statistics 캐릭터 통계 결과
     * @param itemType 아이템 타입 (PET, DECORATION)
     * @param category 카테고리 (CAT, LEFT, RIGHT, BOTTOM)
     * @return 해당 카테고리의 희귀도 합계 (최대값으로 제한됨)
     */
    private static int getCategoryRarity(CharacterStatisticsResult statistics, String itemType, String category) {
        if (statistics.inventoryRarityStatistics() == null) {
            return 0;
        }

        Map<String, Integer> typeMap = statistics.inventoryRarityStatistics().get(itemType);
        if (typeMap == null) {
            return 0;
        }

        Integer rarity = typeMap.get(category);
        if (rarity == null) {
            return 0;
        }

        // 최대값으로 제한
        return Math.min(rarity, MAX_CATEGORY_RARITY);
    }
}