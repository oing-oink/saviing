import PetHud from '@/features/game/pet/components/PetHud';
import PetStatusCard from '@/features/game/pet/components/PetStatusCard';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
  PopoverAnchor,
} from '@/shared/components/ui/popover';
import Cloud from '@/features/game/shared/components/Cloud';
import groom1 from '@/assets/game_bg/groom1.png';
import groom2 from '@/assets/game_bg/groom2.png';

const GamePage = () => {
  return (
    <div className="game relative flex h-screen flex-col overflow-hidden font-galmuri">
      <div className="absolute inset-0 bg-sky-bg">
        <Cloud src={groom1} top={10} left={0} height={5} duration={30} />
        <Cloud src={groom2} top={30} left={20} height={8} duration={65} />
        <Cloud src={groom1} top={60} left={50} height={8} duration={40} />
        <Cloud src={groom2} top={80} left={20} height={6} duration={25} />
      </div>

      <div className="relative flex h-full flex-col">
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
              sideOffset={24}
              className="w-screen max-w-sm p-0"
            >
              <PetStatusCard />
            </PopoverContent>
          </Popover>
        </div>
      </div>
    </div>
  );
};

export default GamePage;
