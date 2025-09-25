import { Card } from '@/shared/components/ui/card';
import { Badge } from '@/shared/components/ui/badge';
import PetStatsPanel from '@/features/savings/components/PetStatsPanel';
import { usePetStatusCard } from '@/features/game/pet/hooks/usePetStatusCard';
import { useGlobalGameBackground } from '@/features/game/shared/components/GlobalGameBackground';
import playButton from '@/assets/game_button/playButton.png';
import CatSprite from '@/features/game/pet/components/CatSprite';
import { Loader2 } from 'lucide-react';
import { PAGE_PATH } from '@/shared/constants/path';
import { useNavigate } from 'react-router-dom';
import { useRef, useEffect } from 'react';
import { useEnterTransitionStore } from '@/features/game/shared/store/useEnterTransitionStore';

interface PetCardProps {
  petId: number;
}

const PetCard = ({ petId }: PetCardProps) => {
  const { petData, isLoading, error, levelClass } = usePetStatusCard(petId);
  const navigate = useNavigate();
  const { setOrigin } = useEnterTransitionStore();
  const { showGameBackground } = useGlobalGameBackground();
  const spriteRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    showGameBackground();
  }, [showGameBackground]);

  if (isLoading) {
    return (
      <Card className="game relative flex w-full flex-col items-center gap-4 overflow-hidden rounded-2xl bg-transparent py-4 font-galmuri shadow">
        <div className="relative z-10 flex h-full items-center justify-center">
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
      <Card className="game relative flex w-full flex-col items-center gap-4 overflow-hidden rounded-2xl bg-transparent py-4 shadow">
        <div className="relative z-10 text-red-500">
          데이터를 불러올 수 없습니다
        </div>
      </Card>
    );
  }

  return (
    <Card className="game relative flex w-full flex-col items-center gap-4 overflow-hidden rounded-2xl bg-transparent py-4 font-galmuri shadow">
      {/* 레벨 배지 */}
      <Badge className={`relative z-10 ${levelClass}`}>Lv{petData.level}</Badge>

      {/* 캐릭터 */}
      <div
        ref={spriteRef}
        className="relative z-10 flex h-28 items-center justify-center"
      >
        <CatSprite
          itemId={petData.itemId}
          currentAnimation="sleep"
          className="scale-400"
        />
      </div>

      {/* 스탯 패널 */}
      <div className="relative z-10">
        <PetStatsPanel
          affection={petData.affection}
          maxAffection={petData.maxAffection}
          hunger={petData.energy}
          maxHunger={petData.maxEnergy}
          exp={petData.exp}
          maxExp={petData.requiredExp}
        />
      </div>

      {/* PLAY 버튼 */}
      <button
        className="relative z-10 mt-2"
        onClick={() => {
          const el = spriteRef.current;
          if (el) {
            const rect = el.getBoundingClientRect();
            const spriteCenterX = rect.left + rect.width / 2;
            const spriteCenterY = rect.top + rect.height / 2;
            const viewportCenterX = window.innerWidth / 2;
            const viewportCenterY = window.innerHeight / 2;
            const startX = spriteCenterX - viewportCenterX;
            const startY = spriteCenterY - viewportCenterY;
            setOrigin({ x: startX, y: startY, scale: 4 });
          } else {
            setOrigin({ x: 0, y: -60, scale: 4 });
          }
          navigate(PAGE_PATH.GAME_ENTER);
        }}
      >
        <img src={playButton} alt="playButton" className="h-12 w-auto" />
      </button>
    </Card>
  );
};

export default PetCard;
