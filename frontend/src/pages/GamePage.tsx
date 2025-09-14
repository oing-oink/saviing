import PetHud from '@/features/game/shared/components/GameHeader';
import PetStatusCard from '@/features/game/pet/components/PetStatusCard';
import CatSprite from '@/features/game/pet/components/CatSprite';
import GameBackground from '@/features/game/shared/components/GameBackground';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
  PopoverAnchor,
} from '@/shared/components/ui/popover';

const GamePage = () => {
  // TODO: API 연결 후 동적으로 관리
  const currentPetId = 10;
  const currentAnimation = 'idle' as const;

  // TODO: 나중에 동적으로 변경할 수 있도록 상태 관리 추가 예정
  // const [petPosition, setPetPosition] = useState({ x: 0, y: 0 });
  // const [currentAnimation, setCurrentAnimation] = useState<PetAnimationState>('idle');

  return (
    <div className="game relative flex h-screen flex-col overflow-hidden font-galmuri">
      <GameBackground />

      <div className="relative flex h-full flex-col">
        <div className="px-3">
          <PetHud />
        </div>

        <div className="flex w-full flex-1 items-center justify-center">
          <Popover>
            <PopoverTrigger asChild>
              <div className="cursor-pointer">
                <CatSprite
                  petId={currentPetId}
                  currentAnimation={currentAnimation}
                  className="scale-300"
                />
              </div>
            </PopoverTrigger>

            <PopoverAnchor className="absolute right-0 bottom-0 left-0" />

            <PopoverContent
              side="top"
              align="center"
              sideOffset={24}
              className="w-screen max-w-sm p-0"
            >
              <PetStatusCard petId={currentPetId} />
            </PopoverContent>
          </Popover>
        </div>
      </div>
    </div>
  );
};

export default GamePage;
