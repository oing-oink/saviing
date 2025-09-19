import { createBrowserRouter } from 'react-router-dom';
import HomePage from '@/pages/HomePage';
import LoginPage from '@/pages/LoginPage';
import SavingsPage from '@/pages/SavingsPage';
import SavingsDetailPage from '@/pages/SavingsDetailPage';
import AccountDetailPage from '@/pages/AccountDetailPage';
import GamePage from '@/pages/GamePage';
import NotFoundPage from '@/pages/NotFoundPage';
import ColorTestPage from '@/pages/ColorTestPage';
import { PAGE_PATH } from '@/shared/constants/path';
import ShopPage from '@/pages/ShopPage';
import GachaPage from '@/pages/GachaPage';
import GachaRollingPage from '@/pages/GachaRollingPage';
import DecoPage from '@/pages/DecoPage';
import WalletPage from '@/pages/WalletPage';
import AccountCreationFunnel from '@/features/savings/components/AccountCreationFunnel';
import SavingsLayout from '@/features/savings/layouts/SavingsLayout';
import SavingsDetailLayout from '@/features/savings/layouts/SavingsDetailLayout';

export const router = createBrowserRouter([
  {
    path: PAGE_PATH.HOME,
    element: <SavingsLayout />,
    children: [
      { index: true, element: <HomePage /> },
      { path: PAGE_PATH.WALLET, element: <WalletPage /> },
    ],
  },
  // Account Creation Funnel Routes
  {
    path: `${PAGE_PATH.ACCOUNT_CREATION}/*`,
    element: <AccountCreationFunnel />,
  },
  { path: PAGE_PATH.LOGIN, element: <LoginPage /> },
  { path: PAGE_PATH.SAVINGS, element: <SavingsPage /> },
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
  { path: PAGE_PATH.GAME, element: <GamePage /> },
  { path: PAGE_PATH.SHOP, element: <ShopPage /> },
  { path: PAGE_PATH.GACHA, element: <GachaPage /> },
  { path: PAGE_PATH.GACHA_ROLLING, element: <GachaRollingPage /> },
  { path: PAGE_PATH.DECO, element: <DecoPage /> },
  { path: PAGE_PATH.NOT_FOUND, element: <NotFoundPage /> },
  { path: PAGE_PATH.COLORTEST, element: <ColorTestPage /> },
]);
