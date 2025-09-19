import TopBar from '@/features/savings/components/TopBar';
import BottomNav from '@/features/savings/components/BottomNav';
import { Outlet } from 'react-router-dom';

const SavingsLayout = () => {
  return (
    <div className="saving mx-auto flex h-dvh w-full max-w-md flex-col bg-violet-50">
      <TopBar />
      <div className="flex-1 overflow-y-auto pb-20">
        <Outlet />
      </div>
      <BottomNav />
    </div>
  );
};

export default SavingsLayout;
