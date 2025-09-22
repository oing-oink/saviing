import {
  Shield,
  HelpCircle,
  Bell,
  LogOut,
  ChevronRight,
  User,
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';
import { PAGE_PATH } from '@/shared/constants/path';

interface ProfileMenuItem {
  icon: React.ComponentType<{ size?: number; className?: string }>;
  label: string;
  description?: string;
  onClick: () => void;
}

const ProfilePage = () => {
  const navigate = useNavigate();
  const { customer, clearAuth } = useCustomerStore();

  // 사용자 정보 (스토어에서 가져오거나 기본값 사용)
  const userInfo = {
    name: customer?.name || '오익준',
    email: 'kim.ssafy@example.com',
    phone: '010-1234-5678',
  };

  const handleLogout = () => {
    // 1. 스토어에서 인증 정보 제거
    clearAuth();

    // 2. 로그인 페이지로 이동
    navigate(PAGE_PATH.LOGIN, { replace: true });
  };

  const profileMenuItems: ProfileMenuItem[] = [
    {
      icon: Shield,
      label: '보안 설정',
      description: '비밀번호, 생체인증 관리',
      onClick: () => console.log('보안 설정'),
    },
    {
      icon: Bell,
      label: '알림 설정',
      description: '푸시 알림, 이메일 알림 설정',
      onClick: () => console.log('알림 설정'),
    },
    {
      icon: HelpCircle,
      label: '고객지원',
      description: '문의하기, 자주 묻는 질문',
      onClick: () => console.log('고객지원'),
    },
  ];

  return (
    <div className="px-5 py-4">
      <div className="flex flex-col gap-6">
        {/* 사용자 정보 카드 */}
        <div className="rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
          <div className="flex items-center gap-4">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-violet-100">
              <User size={32} className="text-violet-600" />
            </div>
            <div className="flex-1">
              <h2 className="text-xl font-semibold text-gray-900">
                {userInfo.name}
              </h2>
              <p className="text-sm text-gray-600">{userInfo.email}</p>
              <p className="text-sm text-gray-600">{userInfo.phone}</p>
            </div>
          </div>
        </div>

        {/* 메뉴 리스트 */}
        <div className="overflow-hidden rounded-2xl border border-gray-100 bg-white shadow-sm">
          {profileMenuItems.map((item, index) => {
            const IconComponent = item.icon;
            return (
              <button
                key={index}
                onClick={item.onClick}
                className="flex w-full items-center gap-4 p-4 text-left transition-colors hover:bg-gray-50 focus:bg-gray-50 focus:outline-none"
              >
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gray-100">
                  <IconComponent size={20} className="text-gray-600" />
                </div>
                <div className="flex-1">
                  <h3 className="font-medium text-gray-900">{item.label}</h3>
                  {item.description && (
                    <p className="text-sm text-gray-500">{item.description}</p>
                  )}
                </div>
                <ChevronRight size={20} className="text-gray-400" />
              </button>
            );
          })}
        </div>

        {/* 로그아웃 버튼 */}
        <button
          onClick={handleLogout}
          className="flex w-full items-center justify-center gap-2 rounded-2xl bg-red-50 p-4 text-red-600 transition-colors hover:bg-red-100 focus:bg-red-100 focus:outline-none"
        >
          <LogOut size={20} />
          <span className="font-medium">로그아웃</span>
        </button>
      </div>
    </div>
  );
};

export default ProfilePage;
