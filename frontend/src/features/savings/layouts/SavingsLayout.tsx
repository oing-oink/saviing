import TopBar from '@/features/savings/components/TopBar';
import BottomNav from '@/features/savings/components/BottomNav';
import { Outlet } from 'react-router-dom';
import { PageScrollArea } from '@/shared/components/layout/PageScrollArea';
import { useGlobalGameBackground } from '@/features/game/shared/components/GlobalGameBackground';

const SavingsLayout = () => {
  const { isGameBackground } = useGlobalGameBackground();

  return (
    <div
      className={`saving mx-auto flex h-dvh w-full max-w-md flex-col ${isGameBackground ? 'bg-transparent' : 'bg-violet-50'}`}
    >
      <TopBar />
      <PageScrollArea className="flex-1">
        <div className="pb-30">
          <Outlet />
        </div>
      </PageScrollArea>
      <BottomNav />
    </div>
  );
};

export default SavingsLayout;
