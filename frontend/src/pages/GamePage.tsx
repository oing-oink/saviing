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
import { useGameQuery } from '@/features/game/shared/query/useGameQuery';
import { useGameStore } from '@/features/game/shared/store/useGameStore';
import { useEffect, useState } from 'react';

const GamePage = () => {
  // 캐릭터 ID 하드코딩
  const characterId = 5;

  // 게임 데이터 조회 및 스토어 관리
  const { data: gameData, isLoading, error } = useGameQuery(characterId);
  const { setGameData } = useGameStore();

  // 게임 데이터가 로드되면 스토어에 저장
  useEffect(() => {
    if (gameData) {
      setGameData(gameData);
    }
  }, [gameData, setGameData]);

  // TODO: API 연결 후 동적으로 관리
  const currentPetId = 7;
  const currentAnimation = 'idle' as const;

  // Popover 상태 관리
  const [isPopoverOpen, setIsPopoverOpen] = useState(false);

  // TODO: 나중에 동적으로 변경할 수 있도록 상태 관리 추가 예정
  // const [petPosition, setPetPosition] = useState({ x: 0, y: 0 });
  // const [currentAnimation, setCurrentAnimation] = useState<PetAnimationState>('idle');

  // 로딩 중이거나 에러가 있는 경우 처리
  if (isLoading || error) {
    // 로딩/에러 상태에서도 기본 UI는 표시 (Coin 컴포넌트가 기본값 0을 표시)
  }

  return (
    <div className="game safeArea relative flex h-dvh touch-none flex-col overflow-hidden font-galmuri">
      <GameBackground />

      <div className="relative flex h-full flex-col">
        <GameHeader />

        <div className="relative mt-4 flex w-full flex-1 justify-center px-4">
          <div className="relative inline-block">
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
                    currentAnimation={currentAnimation}
                    className="scale-250"
                  />
                </button>
              </PopoverTrigger>
              <div
                className={`absolute left-1/2 z-0 h-80 w-80 -translate-x-1/2 -translate-y-1/2 transition-all duration-300 ease-in-out ${
                  isPopoverOpen ? 'top-[30%]' : 'top-1/2'
                }`}
              >
                <Room />
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

        <div className="absolute right-0 bottom-0 z-5 pr-3 pb-5">
          <ElevatorButton />
        </div>
      </div>
    </div>
  );
};

export default GamePage;
