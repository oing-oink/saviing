import closeButton from '@/assets/game_button/closeButton.png';
import infoHeader from '@/assets/game_etc/infoHeader.png';
import { useCharacterStatistics } from '@/features/game/shared/query/useGameQuery';

interface InterestRateModalProps {
  isOpen: boolean;
  onClose: () => void;
  characterId: number;
}

const InterestRateModal = ({
  isOpen,
  onClose,
  characterId,
}: InterestRateModalProps) => {
  const {
    data: statisticsData,
    isLoading,
    error,
  } = useCharacterStatistics(characterId);

  if (!isOpen) {
    return null;
  }

  if (isLoading) {
    return (
      <div className="game fixed inset-0 z-[99999] flex items-center justify-center bg-white/50 font-galmuri">
        <div className="rounded-4xl bg-secondary p-6 shadow-lg">
          <div className="text-center">통계 정보를 불러오는 중...</div>
        </div>
      </div>
    );
  }

  if (error || !statisticsData) {
    return (
      <div className="game fixed inset-0 z-[99999] flex items-center justify-center bg-white/50 font-galmuri">
        <div className="rounded-4xl bg-secondary p-6 shadow-lg">
          <div className="text-center text-red-500">
            통계 정보를 불러올 수 없습니다.
          </div>
          <div className="mt-4 flex justify-center">
            <button
              onClick={onClose}
              className="rounded-lg bg-primary px-8 py-2 text-center font-medium text-white hover:bg-primary/80"
            >
              닫기
            </button>
          </div>
        </div>
      </div>
    );
  }

  // 계산 로직
  const baseRate = 1.5; // 기본 금리 1.5%
  const maxBonusRate = 3.0; // 최대 보너스 3%

  // 펫 레벨 점수 (60% 비중)
  const petWeight = 0.6;
  const petMaxScore = 100; // 최대 레벨 10개 × 최대 레벨 10 = 100
  const petScore = Math.min(statisticsData.topPetLevelSum, petMaxScore);
  const petBonusRate = (petScore / petMaxScore) * maxBonusRate * petWeight;

  // 인벤토리 레어리티 점수 (40% 비중, 각 항목 10%)
  const inventoryWeight = 0.4;
  const itemWeight = inventoryWeight / 4; // CAT, LEFT, RIGHT, BOTTOM 각각 10%
  const maxItemScore = 20; // 각 항목별 최대 20점

  const catScore = Math.min(
    statisticsData.inventoryRarityStatistics.pet.CAT,
    maxItemScore,
  );
  const leftScore = Math.min(
    statisticsData.inventoryRarityStatistics.decoration.LEFT,
    maxItemScore,
  );
  const rightScore = Math.min(
    statisticsData.inventoryRarityStatistics.decoration.RIGHT,
    maxItemScore,
  );
  const bottomScore = Math.min(
    statisticsData.inventoryRarityStatistics.decoration.BOTTOM,
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

  return (
    <div className="game fixed inset-0 z-[99999] flex items-center justify-center bg-white/50 font-galmuri">
      <div className="relative">
        <img
          src={infoHeader}
          alt="itemHeader"
          className="mx-auto -mb-8 w-[50%]"
        />
        <div className="mx-4 max-w-md justify-center rounded-4xl bg-secondary p-6 px-6 shadow-lg">
          <div className="mb-4 flex justify-end">
            <button
              onClick={onClose}
              className="text-gray-500 hover:text-gray-700"
            >
              <img src={closeButton} alt="closeButton" className="w-[60%]" />
            </button>
          </div>

          <div className="flex flex-col items-center">
            <h2 className="mb-6 text-xl font-semibold text-gray-800">
              이자율 계산 상세
            </h2>

            <div className="w-full space-y-4 text-sm">
              {/* 기본 금리 */}
              <div className="flex items-center justify-between rounded-lg bg-white/50 p-3">
                <span className="font-medium text-gray-700">기본 금리</span>
                <span className="font-semibold text-primary">{baseRate}%</span>
              </div>

              {/* 보너스 금리 섹션 */}
              <div className="space-y-2">
                <h3 className="text-center font-medium text-gray-800">
                  보너스 금리 (최대 {maxBonusRate}%)
                </h3>

                {/* 펫 레벨 보너스 */}
                <div className="rounded-lg bg-white/30 p-3">
                  <div className="mb-2 flex items-center justify-between">
                    <span className="text-xs text-gray-600">
                      펫 레벨 합 (비중 60%)
                    </span>
                    <span className="text-xs font-medium text-purple-700">
                      +{petBonusRate.toFixed(3)}%
                    </span>
                  </div>
                  <div className="text-center text-xs text-gray-600">
                    상위 10마리 레벨 합: {statisticsData.topPetLevelSum}점
                  </div>
                </div>

                {/* 인벤토리 레어리티 보너스 */}
                <div className="space-y-2 rounded-lg bg-white/30 p-3">
                  <div className="mb-2 text-center text-xs text-gray-600">
                    인벤토리 레어리티 (비중 40%)
                  </div>

                  <div className="grid grid-cols-2 gap-2 text-xs">
                    <div className="flex justify-between rounded-md bg-primary p-2">
                      <span>펫</span>
                      <span className="font-medium">{catScore}점</span>
                    </div>
                    <div className="flex justify-between rounded-md bg-primary p-2">
                      <span>왼쪽 벽 장식</span>
                      <span className="font-medium">{leftScore}점</span>
                    </div>
                    <div className="flex justify-between rounded-md bg-primary p-2">
                      <span>오른쪽 벽 장식</span>
                      <span className="font-medium">{rightScore}점</span>
                    </div>
                    <div className="flex justify-between rounded-md bg-primary p-2">
                      <span>바닥 장식</span>
                      <span className="font-medium">{bottomScore}점</span>
                    </div>
                  </div>

                  <div className="border-t border-gray-300 pt-1 text-center">
                    <span className="text-xs font-medium text-blue-700">
                      +{totalInventoryBonus.toFixed(3)}%
                    </span>
                  </div>
                </div>
              </div>

              {/* 최종 이자율 */}
              <div className="flex items-center justify-between rounded-lg border-2 border-primary/20 bg-primary/10 p-4">
                <span className="text-base font-semibold text-gray-800">
                  최종 이자율
                </span>
                <span className="text-lg font-bold text-red-400">
                  {finalRate.toFixed(2)}%
                </span>
              </div>
            </div>

            {/* 닫기 버튼 */}
            <div className="mt-6 flex justify-center">
              <button
                onClick={onClose}
                className="rounded-lg bg-primary px-8 py-2 text-center font-medium text-white hover:bg-primary/80"
              >
                확인
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default InterestRateModal;
