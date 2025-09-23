import PetCard from '@/features/game/pet/components/PetCard';
import PromoCarousel from '@/features/savings/components/PromoCarousel';
import SavingCard from '@/features/savings/components/SavingCard';
import GameBackgroundLayout from '@/features/game/shared/layouts/GameBackgroundLayout';

const HomePage = () => {
  return (
    <GameBackgroundLayout>
      <div className="px-5 py-4">
        <div className="flex flex-col items-center gap-4">
          <PromoCarousel />
          <PetCard petId={1} />
          <SavingCard />
        </div>
      </div>
    </GameBackgroundLayout>
  );
};

export default HomePage;
