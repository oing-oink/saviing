import { Badge } from '@/shared/components/ui/badge';
import { Card } from '@/shared/components/ui/card';
import { Button } from '@/shared/components/ui/button';
import StatusProgress from './StatusProgress';
import { usePetStore } from '@/features/game/pet/store/usePetStore';
import { usePetStatusCard } from '@/features/game/pet/hooks/usePetStatusCard';
import feed from '@/assets/game_pet/feed.png';
import play from '@/assets/game_pet/play.png';
import { X } from 'lucide-react';
import { Loader2 } from 'lucide-react'; // shadcn에서 자주 쓰는 로딩 아이콘

const PetStatusCard = () => {
  const { closeStatusCard, inventory } = usePetStore();
  //TODO: api 작성 후 petId로 수정
  const { petData, isLoading, error, levelClass } = usePetStatusCard(1);

  const handleCardClick = (e: React.MouseEvent) => {
    e.stopPropagation();
  };

  return (
    <div className="absolute right-0 bottom-0 left-0 animate-in duration-300 slide-in-from-bottom">
      <Card
        className="mx-6 mb-10 h-[16rem] rounded-t-2xl p-5"
        onClick={handleCardClick}
      >
        {isLoading && (
          <div className="flex items-center justify-center">
            <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
            <span className="ml-2 text-sm text-muted-foreground">
              불러오는 중...
            </span>
          </div>
        )}

        {(error || !petData) && !isLoading && (
          <div className="flex items-center justify-between">
            <div className="text-red-500">데이터를 불러올 수 없습니다</div>
            <button onClick={closeStatusCard}>
              <X />
            </button>
          </div>
        )}

        {petData && !isLoading && !error && (
          <>
            <div className="flex items-center gap-3">
              <Badge className={`${levelClass}`}>Lv{petData.level}</Badge>
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
              <Button
                variant="secondary"
                className="h-20 flex-1 flex-col gap-2"
              >
                <img src={feed} className="h-8 w-8" />
                {inventory.feed}개
              </Button>
              <Button
                variant="secondary"
                className="h-20 flex-1 flex-col gap-2"
              >
                <img src={play} className="h-8 w-8" />
                {inventory.toy}개
              </Button>
            </div>
          </>
        )}
      </Card>
    </div>
  );
};

export default PetStatusCard;
