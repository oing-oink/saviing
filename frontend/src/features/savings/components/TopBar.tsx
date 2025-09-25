import { Bell } from 'lucide-react';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';

const TopBar = () => {
  const { name } = useCustomerStore();

  return (
    <header className="saving sticky top-0 z-50 flex w-full items-center justify-between bg-violet-50 px-6 py-4">
      {/* 왼쪽 로고 */}
      <h1 className="text-2xl font-bold text-primary">Saviing</h1>

      {/* 오른쪽 사용자 정보 + 알림 */}
      <div className="flex items-center gap-4">
        <span className="text-lg font-semibold text-violet-500">{name}님</span>
        <Bell className="h-6 w-6 text-violet-500" />
      </div>
    </header>
  );
};

export default TopBar;
