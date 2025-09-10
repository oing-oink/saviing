import { createBrowserRouter } from 'react-router-dom';
import HomePage from '@/pages/HomePage';
import LoginPage from '@/pages/LoginPage';
import SavingsPage from '@/pages/SavingsPage';
import GamePage from '@/pages/GamePage';
import NotFoundPage from '@/pages/NotFoundPage';
import { PAGE_PATH } from '@/shared/constants/path';
import ShopPage from '@/pages/ShopPage';

export const router = createBrowserRouter([
  { path: PAGE_PATH.HOME, element: <HomePage /> },
  { path: PAGE_PATH.LOGIN, element: <LoginPage /> },
  { path: PAGE_PATH.SAVINGS, element: <SavingsPage /> },
  { path: PAGE_PATH.GAME, element: <GamePage /> },
  { path: PAGE_PATH.SHOP, element: <ShopPage /> },
  { path: PAGE_PATH.NOT_FOUND, element: <NotFoundPage /> },
]);
