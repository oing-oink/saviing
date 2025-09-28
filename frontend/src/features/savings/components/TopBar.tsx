import { Bell } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';
import saviingLogo from '@/assets/saviing_logo.png';

const TopBar = () => {
  const navigate = useNavigate();
  const { name } = useCustomerStore();

  const handleLogoClick = () => {
    navigate(PAGE_PATH.HOME);
  };

  return (
    <header className="saving sticky top-0 z-50 flex w-full items-center justify-between bg-violet-50 px-6 pt-6 pb-4">
      {/* 왼쪽 로고 */}
      <button
        onClick={handleLogoClick}
        className="text-2xl font-bold text-primary transition-colors hover:text-primary/90 focus:ring-2 focus:ring-violet-50 focus:ring-offset-0 focus:outline-none"
      >
        <img src={saviingLogo} alt="saviing_logo" className="h-7 px-0" />
      </button>

      {/* 오른쪽 사용자 정보 + 알림 */}
      <div className="flex items-center gap-4">
        <span className="text-lg font-semibold text-violet-500">{name}님</span>
        <Bell className="h-6 w-6 text-violet-500" />
      </div>
    </header>
  );
};

export default TopBar;
