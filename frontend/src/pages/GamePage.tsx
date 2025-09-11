import PetHud from '@/features/game/pet/components/PetHud';
import PetStatusCard from '@/features/game/pet/components/PetStatusCard';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
  PopoverAnchor,
} from '@/shared/components/ui/popover';
import groom1 from '@/assets/game_bg/groom1.png';
import groom2 from '@/assets/game_bg/groom2.png';

const GamePage = () => {
  return (
    <div className="game relative flex h-screen flex-col bg-sky-bg font-galmuri">
      {/* <img src={groom1} className="absolute top-20 left-1 h-30 w-50" />
      <img src={groom2} className="absolute top-60 left-80 h-20 w-80" />
      <img src={groom1} className="absolute top-100 left-2 h-40 w-80" />
      <img src={groom2} className="absolute top-170 left-64 h-30 w-50" /> */}

      <div className="px-3">
        <PetHud />
      </div>

      <div className="flex w-full flex-1 items-center justify-center">
        <Popover>
          <PopoverTrigger asChild>
            <div className="cursor-pointer text-6xl">🐱</div>
          </PopoverTrigger>

          <PopoverAnchor className="absolute right-0 bottom-0 left-0" />

          <PopoverContent
            side="top"
            align="center"
            sideOffset={16}
            className="w-screen max-w-sm bg-transparent p-0"
          >
            <PetStatusCard />
          </PopoverContent>
        </Popover>
      </div>
    </div>
  );
};

export default GamePage;
