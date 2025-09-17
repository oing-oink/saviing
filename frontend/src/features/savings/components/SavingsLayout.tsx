import TopBar from './TopBar';
import BottomNav from './BottomNav';
import { Outlet } from 'react-router-dom';

const SavingsLayout = () => {
  return (
    <div className="saving mx-auto min-h-dvh w-full max-w-md bg-violet-50">
      <TopBar />
      <div className="pb-20">
        <Outlet />
      </div>
      <BottomNav />
    </div>
  );
};

export default SavingsLayout;
