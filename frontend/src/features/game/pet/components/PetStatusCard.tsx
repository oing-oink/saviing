import { Badge } from '@/shared/components/ui/badge';
import { Card } from '@/shared/components/ui/card';
import { Button } from '@/shared/components/ui/button';
import StatusProgress from './StatusProgress';
import { usePetStore } from '@/features/game/pet/store/usePetStore';
import { usePetStatusCard } from '@/features/game/pet/hooks/usePetStatusCard';
import feed from '@/assets/game_pet/feed.png';
import play from '@/assets/game_pet/play.png';
import { Loader2 } from 'lucide-react';

interface PetStatusCardProps {
  petId: number;
}

/**
 * 펫의 상세 정보를 표시하는 상태 카드 컴포넌트
 *
 * 펫의 레벨, 이름, 포만감, 애정도, 경험치와 아이템 인벤토리를 표시
 */
const PetStatusCard = ({ petId }: PetStatusCardProps) => {
  const { inventory } = usePetStore();
  const { petData, isLoading, error, levelClass } = usePetStatusCard(petId);

  if (isLoading) {
    return (
      <Card className="mx-5 flex min-h-60 items-center justify-center rounded-t-2xl p-4">
        <div className="flex h-full items-center justify-center">
          <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
          <span className="ml-2 text-sm text-muted-foreground">
            불러오는 중...
          </span>
        </div>
      </Card>
    );
  }

  if (error || !petData) {
    return (
      <Card className="mx-5 flex min-h-60 items-center justify-center rounded-t-2xl p-4">
        <div className="flex h-full items-center justify-center">
          <div className="text-red-500">데이터를 불러올 수 없습니다</div>
        </div>
      </Card>
    );
  }

  return (
    <Card className="mx-5 min-h-60 rounded-t-2xl p-4">
      <div className="flex items-center gap-3">
        <Badge className={`${levelClass}`}>Lv{petData.level}</Badge>
        <div>{petData.name}</div>
      </div>
      <div className="space-y-3">
        <div className="flex gap-3">
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
      <div className="flex justify-center gap-4">
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
  );
};

export default PetStatusCard;
