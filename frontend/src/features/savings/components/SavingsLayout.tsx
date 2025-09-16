import TopBar from './TopBar';
import BottomNav from './BottomNav';
import { Outlet } from 'react-router-dom';

const SavingsLayout = () => {
  return (
    <div className="mx-auto min-h-dvh w-full max-w-md bg-violet-50">
      <TopBar />
      <div className="pt-16 pb-20">
        <Outlet />
      </div>
      <BottomNav />
    </div>
  );
};

export default SavingsLayout;
