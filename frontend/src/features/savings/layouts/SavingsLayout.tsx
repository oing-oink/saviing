import TopBar from '@/features/savings/components/TopBar';
import BottomNav from '@/features/savings/components/BottomNav';
import { Outlet } from 'react-router-dom';
import { PageScrollArea } from '@/shared/components/layout/PageScrollArea';

const SavingsLayout = () => {
  return (
    <div className="saving mx-auto flex h-dvh w-full max-w-md flex-col bg-violet-50">
      <TopBar />
      <PageScrollArea className="flex-1 pb-20">
        <Outlet />
      </PageScrollArea>
      <BottomNav />
    </div>
  );
};

export default SavingsLayout;
