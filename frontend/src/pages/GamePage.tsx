import GameHeader from '@/features/game/shared/components/GameHeader';
import GameHeader2 from '@/features/game/shared/components/GameHeader2';
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

const GamePage = () => {
  // TODO: API 연결 후 동적으로 관리
  const currentPetId = 7;
  const currentAnimation = 'idle' as const;

  // TODO: 나중에 동적으로 변경할 수 있도록 상태 관리 추가 예정
  // const [petPosition, setPetPosition] = useState({ x: 0, y: 0 });
  // const [currentAnimation, setCurrentAnimation] = useState<PetAnimationState>('idle');

  return (
    <div className="game safeArea relative flex h-dvh touch-none flex-col overflow-hidden font-galmuri">
      <GameBackground />

      <div className="relative flex h-full flex-col">
        <GameHeader />

        <div className="relative mt-4 flex w-full flex-1 justify-center px-4">
          <div className="relative inline-block">
            <Room />

            <Popover>
              <PopoverTrigger asChild>
                <button
                  className="absolute top-1/3 left-1/2 -translate-x-1/2 -translate-y-1/2 cursor-pointer outline-none"
                  type="button"
                >
                  <CatSprite
                    petId={currentPetId}
                    currentAnimation={currentAnimation}
                    className="scale-250"
                  />
                </button>
              </PopoverTrigger>

              <PopoverAnchor className="absolute right-0 bottom-0 left-0" />

              <PopoverContent
                side="top"
                align="center"
                className="w-screen max-w-sm p-0"
              >
                <PetStatusCard petId={currentPetId} />
              </PopoverContent>
            </Popover>
          </div>
        </div>
        <div className="pb-5">
          <GameHeader2 />
        </div>
      </div>
    </div>
  );
};

export default GamePage;
