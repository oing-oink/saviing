package saviing.game.character.application.dto.result;

import java.util.HashMap;
import java.util.Map;

/**
 * 인벤토리 희귀도 통계 Value Object
 * 캐릭터가 보유한 인벤토리 아이템들의 카테고리별 희귀도 합계 정보를 담는 불변 객체입니다.
 * ItemType(PET, DECORATION) -> Category -> RaritySum 구조로 구성됩니다.
 */
public record InventoryRarityStatistics(
    Map<String, Map<String, Integer>> categoryStatistics
) {

    /**
     * 카테고리별 희귀도 통계를 기반으로 InventoryRarityStatistics를 생성합니다.
     *
     * @param categoryStatistics ItemType별로 그룹화된 카테고리 희귀도 통계
     * @return InventoryRarityStatistics 인스턴스
     */
    public static InventoryRarityStatistics of(Map<String, Map<String, Integer>> categoryStatistics) {
        return new InventoryRarityStatistics(
            categoryStatistics != null ? Map.copyOf(categoryStatistics) : Map.of()
        );
    }

    /**
     * 빈 통계 정보를 가진 InventoryRarityStatistics를 생성합니다.
     *
     * @return 빈 통계를 가진 InventoryRarityStatistics 인스턴스
     */
    public static InventoryRarityStatistics empty() {
        Map<String, Map<String, Integer>> emptyStats = Map.of(
            "PET", Map.of(),
            "DECORATION", Map.of()
        );
        return new InventoryRarityStatistics(emptyStats);
    }
}