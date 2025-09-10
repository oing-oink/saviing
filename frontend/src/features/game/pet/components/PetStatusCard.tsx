import { Badge } from '@/shared/components/ui/badge';
import { Card } from '@/shared/components/ui/card';
import { Button } from '@/shared/components/ui/button';
import StatusProgress from './StatusProgress';
import { usePetStore } from '@/features/game/pet/store/petStore';
import { usePetQuery } from '@/features/game/pet/query/petQuery';
import { defaultPetData } from '@/features/game/pet/types/petTypes';
import feed from '@/assets/game_pet/feed.png';
import play from '@/assets/game_pet/play.png';
import { X } from 'lucide-react';

const PetStatusCard = () => {
  const { closeStatusCard, inventory } = usePetStore();
  const { data: petData = defaultPetData, isLoading, error } = usePetQuery(1);

  // 레벨에 따른 배지 variant 결정
  const getLevelVariant = (
    level: number,
  ):
    | 'level1'
    | 'level2'
    | 'level3'
    | 'level4'
    | 'level5'
    | 'level6'
    | 'level7'
    | 'level8'
    | 'level9'
    | 'level10' => {
    const levelIndex = Math.min(Math.max(level, 1), 10); // 1-10 범위로 제한
    return `level${levelIndex}` as
      | 'level1'
      | 'level2'
      | 'level3'
      | 'level4'
      | 'level5'
      | 'level6'
      | 'level7'
      | 'level8'
      | 'level9'
      | 'level10';
  };

  const handleCardClick = (e: React.MouseEvent) => {
    e.stopPropagation(); // 배경 클릭 이벤트 방지
  };

  // 로딩 중일 때
  if (isLoading) {
    return (
      <div className="absolute right-0 bottom-0 left-0 animate-in duration-300 slide-in-from-bottom">
        <Card
          className="mx-6 mb-10 rounded-t-2xl p-5"
          onClick={handleCardClick}
        >
          <div className="flex items-center justify-center p-8">
            <div className="text-gray-500">로딩 중...</div>
          </div>
        </Card>
      </div>
    );
  }

  // 에러 발생 시
  if (error) {
    return (
      <div className="absolute right-0 bottom-0 left-0 animate-in duration-300 slide-in-from-bottom">
        <Card
          className="mx-6 mb-10 rounded-t-2xl p-5"
          onClick={handleCardClick}
        >
          <div className="flex items-center justify-between">
            <div className="text-red-500">데이터를 불러올 수 없습니다</div>
            <button onClick={closeStatusCard} className="ml-auto">
              <X />
            </button>
          </div>
        </Card>
      </div>
    );
  }

  return (
    <div className="absolute right-0 bottom-0 left-0 animate-in duration-300 slide-in-from-bottom">
      <Card className="mx-6 mb-10 rounded-t-2xl p-5" onClick={handleCardClick}>
        <div className="flex items-center gap-3">
          <Badge variant={getLevelVariant(petData.level)}>
            Lv{petData.level}
          </Badge>
          <div>{petData.name}</div>
          <button onClick={closeStatusCard} className="ml-auto">
            <X />
          </button>
        </div>
        <div className="space-y-4">
          <div className="flex gap-4">
            <StatusProgress
              label="포만감"
              value={petData.energy}
              maxValue={petData.maxEnergy}
              className="progress-energy"
            />
            <StatusProgress
              label="애정도"
              value={petData.affection}
              maxValue={petData.maxAffection}
              className="progress-affection"
            />
          </div>
          <div>
            <StatusProgress
              label="경험치"
              value={petData.exp}
              maxValue={petData.requiredExp}
              className="progress-experience"
            />
          </div>
        </div>
        <div className="flex justify-center gap-5">
          <Button variant="secondary" className="h-20 flex-1 flex-col gap-2">
            <img src={feed} className="h-8 w-8" />
            {inventory.feed}개
          </Button>
          <Button variant="secondary" className="h-20 flex-1 flex-col gap-2">
            <img src={play} className="h-8 w-8" />
            {inventory.toy}개
          </Button>
        </div>
      </Card>
    </div>
  );
};

export default PetStatusCard;
