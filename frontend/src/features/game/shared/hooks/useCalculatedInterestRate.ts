import { useCharacterStatistics } from '@/features/game/shared/query/useGameQuery';

/**
 * 캐릭터 통계를 기반으로 계산된 최종 이자율을 반환하는 훅
 *
 * InterestRateModal과 동일한 로직으로 이자율을 계산합니다.
 *
 * @param characterId - 캐릭터 ID
 * @returns 계산된 최종 이자율 (퍼센트 단위)
 */
export const useCalculatedInterestRate = (characterId?: number) => {
  const {
    data: statisticsData,
    isLoading,
    error,
  } = useCharacterStatistics(characterId);

  if (isLoading || error || !statisticsData) {
    return {
      finalRate: null,
      isLoading,
      error,
    };
  }

  // InterestRateModal과 동일한 계산 로직
  const baseRate = 1.5; // 기본 금리 1.5%
  const maxBonusRate = 3.0; // 최대 보너스 3%

  // 펫 레벨 점수 (60% 비중)
  const petWeight = 0.6;
  const petMaxScore = 100; // 최대 레벨 10개 × 최대 레벨 10 = 100
  const petScore = Math.min(statisticsData.topPetLevelSum || 0, petMaxScore);
  const petBonusRate = (petScore / petMaxScore) * maxBonusRate * petWeight;

  // 인벤토리 레어리티 점수 (40% 비중, 각 항목 10%)
  const inventoryWeight = 0.4;
  const itemWeight = inventoryWeight / 4; // CAT, LEFT, RIGHT, BOTTOM 각각 10%
  const maxItemScore = 20; // 각 항목별 최대 20점

  const catScore = Math.min(
    statisticsData.inventoryRarityStatistics?.pet?.CAT || 0,
    maxItemScore,
  );
  const leftScore = Math.min(
    statisticsData.inventoryRarityStatistics?.decoration?.LEFT || 0,
    maxItemScore,
  );
  const rightScore = Math.min(
    statisticsData.inventoryRarityStatistics?.decoration?.RIGHT || 0,
    maxItemScore,
  );
  const bottomScore = Math.min(
    statisticsData.inventoryRarityStatistics?.decoration?.BOTTOM || 0,
    maxItemScore,
  );

  const catBonusRate = (catScore / maxItemScore) * maxBonusRate * itemWeight;
  const leftBonusRate = (leftScore / maxItemScore) * maxBonusRate * itemWeight;
  const rightBonusRate =
    (rightScore / maxItemScore) * maxBonusRate * itemWeight;
  const bottomBonusRate =
    (bottomScore / maxItemScore) * maxBonusRate * itemWeight;

  const totalInventoryBonus =
    catBonusRate + leftBonusRate + rightBonusRate + bottomBonusRate;
  const totalBonusRate = petBonusRate + totalInventoryBonus;
  const finalRate = baseRate + totalBonusRate;
  const finalCalculatedRate = isNaN(finalRate) ? baseRate : finalRate;

  // 디버깅용 로그 (개발 환경에서만)
  if (process.env.NODE_ENV === 'development') {
    console.log('useCalculatedInterestRate Debug:', {
      characterId,
      statisticsData,
      baseRate,
      petScore,
      petBonusRate,
      catScore,
      leftScore,
      rightScore,
      bottomScore,
      totalInventoryBonus,
      totalBonusRate,
      finalRate,
      finalCalculatedRate,
    });
  }

  return {
    finalRate: finalCalculatedRate,
    isLoading: false,
    error: null,
  };
};
