import type { ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft } from 'lucide-react';

interface DepositLayoutProps {
  children: ReactNode;
}

const DepositLayout = ({ children }: DepositLayoutProps) => {
  const navigate = useNavigate();

  const handleBackClick = () => {
    navigate(-1);
  };

  return (
    <div className="saving mx-auto flex h-dvh w-full max-w-md flex-col bg-white">
      <div className="border-b px-6 py-4">
        <div className="flex items-center justify-start">
          <button
            onClick={handleBackClick}
            className="flex items-center text-gray-600 hover:text-gray-900"
          >
            <ChevronLeft className="h-5 w-5" />
          </button>
        </div>
      </div>
      <div className="flex-1 overflow-y-auto">{children}</div>
    </div>
  );
};

export default DepositLayout;
