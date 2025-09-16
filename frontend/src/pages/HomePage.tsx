import BottomNav from '@/features/savings/components/BottomNav';
import PetCard from '@/features/savings/components/PetCard';
import PromoCarousel from '@/features/savings/components/PromoCarousel';
import SavingCard from '@/features/savings/components/SavingCard';
import TopBar from '@/features/savings/components/TopBar';

const HomePage = () => {
  return (
    <div className="flex min-h-screen flex-col bg-violet-50">
      <TopBar />
      <div className="flex-1 pb-20">
        <div className="flex justify-center py-4">
          <div className="w-[90%]">
            <PromoCarousel />
          </div>
        </div>
        <div className="flex items-center justify-center px-4">
          <PetCard petId={1} />
        </div>
        <div className="flex items-center justify-center px-4">
          <SavingCard />
        </div>
      </div>
      <BottomNav />
    </div>
  );
};

export default HomePage;
