import GameHeader from '@/features/game/shared/components/GameHeader';
import ElevatorButton from '@/features/game/shared/components/ElevatorButton';
import PetStatusCard from '@/features/game/pet/components/PetStatusCard';
import CatSprite from '@/features/game/pet/components/CatSprite';
import GameBackground from '@/features/game/shared/components/GameBackground';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
  PopoverAnchor,
} from '@/shared/components/ui/popover';
import Room from '@/features/game/room/Room';
import { useState } from 'react';
import { usePetStore } from '@/features/game/pet/store/usePetStore';

const GamePage = () => {
  // TODO: API 연결 후 동적으로 관리
  const currentPetId = 9;
  const { behavior, setBehavior } = usePetStore();

  // Popover 상태 관리
  const [isPopoverOpen, setIsPopoverOpen] = useState(false);

  // 애니메이션 완료 시 idle로 복귀
  const handleAnimationComplete = (animation: string) => {
    if (animation === 'sitting' || animation === 'jump') {
      setBehavior({
        currentAnimation: 'idle',
      });
    }
  };

  return (
    <div className="game safeArea relative flex h-dvh touch-none flex-col overflow-hidden font-galmuri">
      <GameBackground />

      <div className="relative flex h-full flex-col">
        <div className="relative z-10">
          <GameHeader />
        </div>

        <div className="relative flex w-full flex-1 justify-center">
          <div className="relative inline-block w-full">
            <Popover open={isPopoverOpen} onOpenChange={setIsPopoverOpen}>
              <PopoverTrigger asChild>
                <button
                  className={`absolute left-1/2 z-10 -translate-x-1/2 -translate-y-1/2 cursor-pointer transition-all duration-300 ease-in-out outline-none ${
                    isPopoverOpen ? 'top-[30%]' : 'top-1/2'
                  }`}
                  type="button"
                >
                  <CatSprite
                    petId={currentPetId}
                    currentAnimation={behavior.currentAnimation}
                    className="scale-250"
                    onAnimationComplete={handleAnimationComplete}
                  />
                </button>
              </PopoverTrigger>
              <div
                className={`absolute left-1/2 z-0 w-full -translate-x-1/2 -translate-y-1/2 transition-all duration-300 ease-in-out ${
                  isPopoverOpen ? 'top-[30%]' : 'top-1/2'
                }`}
              >
                <Room mode="readonly" gridType={null} />
              </div>

              <PopoverAnchor className="absolute right-0 bottom-0 left-0" />

              <PopoverContent
                side="top"
                align="center"
                sideOffset={16}
                className="w-screen max-w-md p-0"
              >
                <PetStatusCard petId={currentPetId} />
              </PopoverContent>
            </Popover>
          </div>
        </div>

        <div className="absolute right-0 bottom-0 z-10 pr-3 pb-5">
          <ElevatorButton />
        </div>
      </div>
    </div>
  );
};

export default GamePage;
