import PetCard from '@/features/game/pet/components/PetCard';
import PromoCarousel from '@/features/savings/components/PromoCarousel';
import SavingCard from '@/features/savings/components/SavingCard';
import { useEffect } from 'react';
import { useGlobalGameBackground } from '@/features/game/shared/components/GlobalGameBackground';

const HomePage = () => {
  const { showGameBackground } = useGlobalGameBackground();

  useEffect(() => {
    showGameBackground();
  }, [showGameBackground]);

  return (
    <div className="px-5 py-4">
      <div className="flex flex-col items-center gap-4">
        <PromoCarousel />
        <PetCard petId={1} />
        <SavingCard />
      </div>
    </div>
  );
};

export default HomePage;
