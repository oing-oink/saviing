import type { RouteObject } from 'react-router-dom';
import ProtectedRoute from '@/shared/components/common/ProtectedRoute';
import {
  PAGE_PATH,
  ACCOUNT_CREATION_STEPS_PATH,
} from '@/shared/constants/path';
import AccountCreationFunnel from '@/features/savings/components/AccountCreationFunnel';
import SavingsLayout from '@/features/savings/layouts/SavingsLayout';
import SavingsDetailLayout from '@/features/savings/layouts/SavingsDetailLayout';
import HomePage from '@/pages/HomePage';
import WalletPage from '@/pages/WalletPage';
import SavingsPage from '@/pages/SavingsPage';
import SavingsDetailPage from '@/pages/SavingsDetailPage';
import AccountDetailPage from '@/pages/AccountDetailPage';

// 독립 전체 페이지 (레이아웃 없음)
const independentPages = [
  { path: PAGE_PATH.SAVINGS, element: <SavingsPage /> },
  { path: PAGE_PATH.ACCOUNT_CREATION, element: <AccountCreationFunnel /> },
  ...Object.values(ACCOUNT_CREATION_STEPS_PATH).map(path => ({
    path,
    element: <AccountCreationFunnel />,
  })),
];

// 메인 레이아웃 페이지
const homePages = [
  { path: '', element: <HomePage /> },
  { path: 'wallet', element: <WalletPage /> },
];

// 상세 레이아웃 페이지
const detailPages = [
  {
    path: PAGE_PATH.SAVINGS_DETAIL_WITH_ID,
    element: <SavingsDetailLayout title="적금 상세" />,
    children: [{ index: true, element: <SavingsDetailPage /> }],
  },
  {
    path: PAGE_PATH.ACCOUNT_DETAIL_WITH_ID,
    element: <SavingsDetailLayout title="계좌 상세" />,
    children: [{ index: true, element: <AccountDetailPage /> }],
  },
];

export const savingsRoutes: RouteObject[] = [
  // 독립 페이지들
  ...independentPages.map(({ path, element }) => ({
    path,
    element: <ProtectedRoute>{element}</ProtectedRoute>,
  })),

  // 상세 페이지들
  ...detailPages.map(({ path, element, children }) => ({
    path,
    element: <ProtectedRoute>{element}</ProtectedRoute>,
    children,
  })),

  // 홈 레이아웃
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <SavingsLayout />
      </ProtectedRoute>
    ),
    children: homePages,
  },
];
