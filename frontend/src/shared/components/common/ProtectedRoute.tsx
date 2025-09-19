import { Navigate, useLocation } from 'react-router-dom';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';
import { PAGE_PATH } from '@/shared/constants/path';

interface ProtectedRouteProps {
  children: React.ReactNode;
}

const ProtectedRoute = ({ children }: ProtectedRouteProps) => {
  const { isAuthenticated } = useCustomerStore();
  const location = useLocation();

  // 인증되지 않은 경우 온보딩 페이지로 리다이렉트
  if (!isAuthenticated) {
    return (
      <Navigate to={PAGE_PATH.ONBOARDING} state={{ from: location }} replace />
    );
  }

  return <>{children}</>;
};

export default ProtectedRoute;
