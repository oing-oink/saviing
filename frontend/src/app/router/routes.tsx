import { createBrowserRouter, Outlet } from 'react-router-dom';
import { customerStore } from '@/features/auth/store/useCustomerStore';
import ProtectedRoute from '@/shared/components/common/ProtectedRoute';
import SavingsLayout from '@/features/savings/components/SavingsLayout';
import { PAGE_PATH } from '@/shared/constants/path';
import AccountCreationFunnel from '@/features/savings/components/AccountCreationFunnel';

// Pages
import HomePage from '@/pages/HomePage';
import WalletPage from '@/pages/WalletPage';
import LoginPage from '@/pages/LoginPage';
import OnboardingPage from '@/pages/OnboardingPage';
import AuthCallbackPage from '@/pages/AuthCallbackPage';
import SavingsPage from '@/pages/SavingsPage';
import GamePage from '@/pages/GamePage';
import ShopPage from '@/pages/ShopPage';
import GachaPage from '@/pages/GachaPage';
import GachaRollingPage from '@/pages/GachaRollingPage';
import DecoPage from '@/pages/DecoPage';
import ColorTestPage from '@/pages/ColorTestPage';
import NotFoundPage from '@/pages/NotFoundPage';

// 앱 시작 시 인증 정보 로드하는 loader 함수
const rootLoader = async () => {
  customerStore.getState().loadFromSession();
  return null;
};

// SavingsLayout을 사용하는 보호된 라우트들
const savingsLayoutRoutes = [
  { path: '', element: <HomePage /> }, // '/' 경로
  { path: 'wallet', element: <WalletPage /> }, // '/wallet' 경로
];

// 레이아웃 없는 보호된 라우트들
const protectedRoutesWithoutLayout = [
  { path: PAGE_PATH.SAVINGS, element: <SavingsPage /> },
  { path: PAGE_PATH.GAME, element: <GamePage /> },
  { path: PAGE_PATH.SHOP, element: <ShopPage /> },
  { path: PAGE_PATH.GACHA, element: <GachaPage /> },
  { path: PAGE_PATH.GACHA_ROLLING, element: <GachaRollingPage /> },
  { path: PAGE_PATH.DECO, element: <DecoPage /> },
  { path: PAGE_PATH.ACCOUNT_CREATION, element: <AccountCreationFunnel /> },
];

// 공개 라우트 정의 (인증 불필요)
const publicRoutes = [
  { path: PAGE_PATH.LOGIN, element: <LoginPage /> },
  { path: PAGE_PATH.ONBOARDING, element: <OnboardingPage /> },
  { path: PAGE_PATH.AUTH_CALLBACK, element: <AuthCallbackPage /> },
  { path: PAGE_PATH.COLORTEST, element: <ColorTestPage /> },
];

// 라우터 생성
export const router = createBrowserRouter([
  {
    path: '/',
    element: <Outlet />,
    loader: rootLoader, // 최상위에서 한 번만 인증 정보 로드
    children: [
      // 공개 라우트
      ...publicRoutes,

      // SavingsLayout을 사용하는 보호된 라우트들
      {
        path: '/',
        element: (
          <ProtectedRoute>
            <SavingsLayout />
          </ProtectedRoute>
        ),
        children: savingsLayoutRoutes,
      },

      // 레이아웃이 없는 보호된 라우트들
      ...protectedRoutesWithoutLayout.map(({ path, element }) => ({
        path,
        element: <ProtectedRoute>{element}</ProtectedRoute>,
      })),

      // Fallback
      { path: '*', element: <NotFoundPage /> },
    ],
  },
]);
