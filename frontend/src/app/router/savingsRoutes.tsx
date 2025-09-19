import type { RouteObject } from 'react-router-dom';
import { Outlet } from 'react-router-dom';
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
import ProductsPage from '@/pages/ProductsPage';
import SavingsPage from '@/pages/SavingsPage';
import SavingsDetailPage from '@/pages/SavingsDetailPage';
import AccountDetailPage from '@/pages/AccountDetailPage';

// 독립 전체 페이지 (순수 라우트)
const independentPages: RouteObject[] = [
  { path: PAGE_PATH.SAVINGS, element: <SavingsPage /> },
  { path: PAGE_PATH.ACCOUNT_CREATION, element: <AccountCreationFunnel /> },
  {
    path: ACCOUNT_CREATION_STEPS_PATH.START,
    element: <AccountCreationFunnel />,
  },
  {
    path: ACCOUNT_CREATION_STEPS_PATH.PRODUCT_TYPE,
    element: <AccountCreationFunnel />,
  },
  {
    path: ACCOUNT_CREATION_STEPS_PATH.USER_INFO,
    element: <AccountCreationFunnel />,
  },
  {
    path: ACCOUNT_CREATION_STEPS_PATH.AUTH,
    element: <AccountCreationFunnel />,
  },
  {
    path: ACCOUNT_CREATION_STEPS_PATH.TERMS,
    element: <AccountCreationFunnel />,
  },
  {
    path: ACCOUNT_CREATION_STEPS_PATH.SET_CONDITION,
    element: <AccountCreationFunnel />,
  },
  {
    path: ACCOUNT_CREATION_STEPS_PATH.CONFIRM,
    element: <AccountCreationFunnel />,
  },
  {
    path: ACCOUNT_CREATION_STEPS_PATH.COMPLETE,
    element: <AccountCreationFunnel />,
  },
];

// 상세 레이아웃 페이지 (순수 라우트)
const detailPages: RouteObject[] = [
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

// 메인 레이아웃 페이지
const homePages: RouteObject[] = [
  { index: true, element: <HomePage /> },
  { path: 'wallet', element: <WalletPage /> },
  { path: 'products', element: <ProductsPage /> },
];

export const savingsRoutes: RouteObject[] = [
  // 홈 레이아웃 (index 라우트 포함)
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <SavingsLayout />
      </ProtectedRoute>
    ),
    children: homePages,
  },
  // 독립 페이지들을 ProtectedRoute로 감싸기
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <Outlet />
      </ProtectedRoute>
    ),
    children: independentPages,
  },
  // 상세 페이지들을 ProtectedRoute로 감싸기
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <Outlet />
      </ProtectedRoute>
    ),
    children: detailPages,
  },
];
