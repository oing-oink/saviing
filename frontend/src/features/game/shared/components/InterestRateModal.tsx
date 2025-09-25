import closeButton from '@/assets/game_button/closeButton.png';
import infoHeader from '@/assets/game_etc/infoHeader.png';
import { useCharacterStatistics } from '@/features/game/shared/query/useGameQuery';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogOverlay,
  DialogPortal,
} from '@/shared/components/ui/dialog';

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

  const handleOpenChange = (open: boolean) => {
    if (!open) {
      onClose();
    }
  };

  if (isLoading) {
    return (
      <Dialog open={isOpen} onOpenChange={handleOpenChange}>
        <DialogPortal>
          <DialogOverlay className="bg-transparent backdrop-blur-sm backdrop-brightness-110" />
          <DialogContent
            className="game max-w-xs rounded-4xl bg-secondary p-6 font-galmuri"
            showCloseButton={false}
          >
            <DialogDescription className="sr-only">
              통계 정보를 불러오고 있습니다
            </DialogDescription>
            <div className="text-center">통계 정보를 불러오는 중...</div>
          </DialogContent>
        </DialogPortal>
      </Dialog>
    );
  }

  if (error || !statisticsData) {
    return (
      <Dialog open={isOpen} onOpenChange={handleOpenChange}>
        <DialogPortal>
          <DialogOverlay className="bg-transparent backdrop-blur-sm backdrop-brightness-110" />
          <DialogContent
            className="game max-w-xs rounded-4xl bg-secondary p-6 font-galmuri"
            showCloseButton={false}
          >
            <DialogDescription className="sr-only">
              통계 정보를 불러오는 중 오류가 발생했습니다
            </DialogDescription>
            <div className="text-center text-red-500">
              통계 정보를 불러올 수 없습니다.
            </div>
            <div className="mt-4 flex justify-center">
              <button
                onClick={onClose}
                className="rounded-lg bg-primary px-8 py-2 text-center font-medium text-white hover:bg-primary/80 active:scale-95 active:brightness-90"
              >
                닫기
              </button>
            </div>
          </DialogContent>
        </DialogPortal>
      </Dialog>
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
    <Dialog open={isOpen} onOpenChange={handleOpenChange}>
      <DialogPortal>
        <DialogOverlay className="bg-transparent backdrop-blur-sm backdrop-brightness-110" />
        <DialogContent
          className="game max-w-xs border-0 bg-transparent p-0 font-galmuri shadow-none"
          showCloseButton={false}
        >
          <DialogDescription className="sr-only">
            펫과 인벤토리 레어리티를 기반으로 계산된 이자율 상세 정보
          </DialogDescription>
          <div className="relative">
            <img
              src={infoHeader}
              alt="itemHeader"
              className="absolute top-0 left-1/2 z-10 w-44 -translate-x-1/2 -translate-y-1/2"
            />
            <div className="rounded-4xl bg-secondary px-5 pt-8 pb-4 shadow-xl">
              <div className="mb-3 flex justify-end">
                <button
                  onClick={onClose}
                  className="text-gray-500 hover:text-gray-700 active:scale-95 active:brightness-90"
                >
                  <img src={closeButton} alt="closeButton" className="w-7" />
                </button>
              </div>

              <div className="flex flex-col items-center">
                <DialogHeader className="mb-4">
                  <DialogTitle className="text-lg font-semibold text-gray-800">
                    이자율 계산 상세
                  </DialogTitle>
                </DialogHeader>

                <div className="w-full space-y-3 text-base">
                  {/* 기본 금리 */}
                  <div className="flex items-center justify-between rounded-lg bg-white/50 p-2.5">
                    <span className="font-medium text-gray-700">기본 금리</span>
                    <span className="font-semibold text-primary">
                      {baseRate}%
                    </span>
                  </div>

                  {/* 보너스 금리 섹션 */}
                  <div className="space-y-2">
                    <h3 className="text-center font-medium text-gray-800">
                      보너스 금리 (최대 {maxBonusRate}%)
                    </h3>

                    {/* 펫 레벨 보너스 */}
                    <div className="rounded-lg bg-white/30 p-2.5">
                      <div className="mb-1.5 flex items-center justify-between">
                        <span className="text-sm text-gray-600">
                          펫 레벨 합 (비중 60%)
                        </span>
                        <span className="text-sm font-medium text-purple-700">
                          +{petBonusRate.toFixed(3)}%
                        </span>
                      </div>
                      <div className="text-center text-sm text-gray-600">
                        상위 10마리 레벨 합: {statisticsData.topPetLevelSum}점
                      </div>
                    </div>

                    {/* 인벤토리 레어리티 보너스 */}
                    <div className="space-y-1.5 rounded-lg bg-white/30 p-2.5">
                      <div className="mb-1.5 text-center text-sm text-gray-600">
                        인벤토리 레어리티 (비중 40%)
                      </div>

                      <div className="grid grid-cols-2 gap-1.5 text-sm">
                        <div className="flex justify-between rounded-md bg-primary p-1.5">
                          <span>펫</span>
                          <span className="font-medium">{catScore}점</span>
                        </div>
                        <div className="flex justify-between rounded-md bg-primary p-1.5">
                          <span>왼쪽 벽</span>
                          <span className="font-medium">{leftScore}점</span>
                        </div>
                        <div className="flex justify-between rounded-md bg-primary p-1.5">
                          <span>오른쪽 벽</span>
                          <span className="font-medium">{rightScore}점</span>
                        </div>
                        <div className="flex justify-between rounded-md bg-primary p-1.5">
                          <span>바닥</span>
                          <span className="font-medium">{bottomScore}점</span>
                        </div>
                      </div>

                      <div className="border-t border-gray-300 pt-1 text-center">
                        <span className="text-sm font-medium text-blue-700">
                          +{totalInventoryBonus.toFixed(3)}%
                        </span>
                      </div>
                    </div>
                  </div>

                  {/* 최종 이자율 */}
                  <div className="flex items-center justify-between rounded-lg border-2 border-primary/20 bg-primary/10 p-2.5">
                    <span className="text-lg font-semibold text-gray-800">
                      최종 이자율
                    </span>
                    <span className="text-xl font-bold text-red-400">
                      {finalRate.toFixed(2)}%
                    </span>
                  </div>
                </div>

                {/* 닫기 버튼 */}
                <div className="mt-4 flex justify-center">
                  <button
                    onClick={onClose}
                    className="rounded-lg bg-primary px-8 py-2 text-center font-medium text-white hover:bg-primary/80 active:scale-95 active:brightness-90"
                  >
                    확인
                  </button>
                </div>
              </div>
            </div>
          </div>
        </DialogContent>
      </DialogPortal>
    </Dialog>
  );
};

export default InterestRateModal;
