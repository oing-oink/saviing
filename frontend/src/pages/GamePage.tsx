import PetHud from '@/features/game/pet/components/PetHud';
import PetStatusCard from '@/features/game/pet/components/PetStatusCard';
import { usePetStore } from '@/features/game/pet/store/petStore';
import { usePetQuery } from '@/features/game/pet/query/petQuery';
import groom1 from '@/assets/game_bg/groom1.png';
import groom2 from '@/assets/game_bg/groom2.png';

const GamePage = () => {
  const { isStatusCardOpen, closeStatusCard, openStatusCard } = usePetStore();

  // 펫 데이터는 query로 관리 (프리페치)
  usePetQuery(1);

  return (
    <div className="game relative flex h-screen flex-col overflow-hidden bg-sky-bg font-galmuri">
      <img src={groom1} className="absolute top-20 left-1 h-30 w-50" />
      <img src={groom2} className="absolute top-60 left-80 h-20 w-80" />
      <img src={groom1} className="absolute top-100 left-2 h-40 w-80" />
      <img src={groom2} className="absolute top-170 left-64 h-30 w-50" />

      <div className="px-3">
        <PetHud />
      </div>
      <div className="flex flex-1 items-center justify-center">
        <div className="z-10 cursor-pointer text-6xl" onClick={openStatusCard}>
          🐱
        </div>
      </div>

      {isStatusCardOpen && (
        <div className="absolute inset-0 z-50" onClick={closeStatusCard}>
          <PetStatusCard />
        </div>
      )}
    </div>
  );
};

export default GamePage;
