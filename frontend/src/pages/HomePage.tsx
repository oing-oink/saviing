import PetCard from '@/features/game/pet/components/PetCard';
import PromoCarousel from '@/features/savings/components/PromoCarousel';
import SavingCard from '@/features/savings/components/SavingCard';

const HomePage = () => {
  return (
    <div className="flex min-h-screen flex-col">
      <div className="flex-1 px-5">
        <div className="flex flex-col items-center gap-4">
          <PromoCarousel />
          <PetCard petId={1} />
          <SavingCard />
        </div>
      </div>
    </div>
  );
};

export default HomePage;
