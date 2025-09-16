import BottomNav from '@/features/savings/components/BottomNav';
import PetCard from '@/features/savings/components/PetCard';
import PromoCarousel from '@/features/savings/components/PromoCarousel';
import SavingCard from '@/features/savings/components/SavingCard';
import TopBar from '@/features/savings/components/TopBar';

const HomePage = () => {
  return (
    <div className="flex min-h-screen flex-col bg-violet-50">
      <TopBar />
      <div className="flex-1 p-5">
        <div className="flex flex-col items-center gap-4">
          <PromoCarousel />
          <PetCard petId={1} />
          <SavingCard />
        </div>
      </div>
      <BottomNav />
    </div>
  );
};

export default HomePage;
