import DetailTopBar from './DetailTopBar';
import { Outlet } from 'react-router-dom';

interface SavingsDetailLayoutProps {
  title: string;
}

const SavingsDetailLayout = ({ title }: SavingsDetailLayoutProps) => {
  return (
    <div className="saving mx-auto min-h-dvh w-full max-w-md bg-secondary">
      <DetailTopBar title={title} />
      <div>
        <Outlet />
      </div>
    </div>
  );
};

export default SavingsDetailLayout;
