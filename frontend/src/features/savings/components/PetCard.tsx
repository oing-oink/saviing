import { Card } from '@/shared/components/ui/card';
import { Badge } from '@/shared/components/ui/badge';
import PetStatsPanel from '@/features/savings/components/PetStatsPanel';
import { usePetStatusCard } from '@/features/game/pet/hooks/usePetStatusCard';
import playButton from '@/assets/game_button/playButton.png';
import sampleCat from '@/assets/sampleCat.png';
import { Loader2 } from 'lucide-react';

interface PetCardProps {
  petId: number;
}

const PetCard = ({ petId }: PetCardProps) => {
  const { petData, isLoading, error, levelClass } = usePetStatusCard(petId);

  if (isLoading) {
    return (
      <Card className="game flex w-full flex-col items-center gap-4 rounded-2xl bg-white py-4 font-galmuri shadow">
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
      <Card className="game flex w-full flex-col items-center gap-4 rounded-2xl bg-white py-4 shadow">
        <div className="text-red-500">데이터를 불러올 수 없습니다</div>
      </Card>
    );
  }

  return (
    <Card className="game flex w-full flex-col items-center gap-4 rounded-2xl bg-white py-4 font-galmuri shadow">
      {/* 레벨 배지 */}
      <Badge className={`${levelClass}`}>Lv{petData.level}</Badge>

      {/* 캐릭터 */}
      <img
        src={sampleCat}
        alt={petData.name}
        className="image-render-pixel h-28 w-auto"
      />

      {/* 스탯 패널 */}
      <PetStatsPanel
        affection={petData.affection}
        maxAffection={petData.maxAffection}
        hunger={petData.energy}
        maxHunger={petData.maxEnergy}
        exp={petData.exp}
        maxExp={petData.requiredExp}
      />

      {/* PLAY 버튼 */}
      <button className="mt-2">
        <img src={playButton} alt="playButton" className="h-12 w-auto" />
      </button>
    </Card>
  );
};

export default PetCard;
