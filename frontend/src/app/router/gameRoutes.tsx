import type { RouteObject } from 'react-router-dom';
import { Outlet } from 'react-router-dom';
import ProtectedRoute from '@/shared/components/common/ProtectedRoute';
import { PAGE_PATH } from '@/shared/constants/path';
import GamePage from '@/pages/GamePage';
import ShopPage from '@/pages/ShopPage';
import GachaPage from '@/pages/GachaPage';
import GachaRollingPage from '@/pages/GachaRollingPage';
import DecoPage from '@/pages/DecoPage';

// 게임 관련 페이지들 (순수 라우트)
const gamePages: RouteObject[] = [
  { path: PAGE_PATH.GAME, element: <GamePage /> },
  { path: PAGE_PATH.SHOP, element: <ShopPage /> },
  { path: PAGE_PATH.GACHA, element: <GachaPage /> },
  { path: PAGE_PATH.GACHA_ROLLING, element: <GachaRollingPage /> },
  { path: PAGE_PATH.DECO, element: <DecoPage /> },
];

export const gameRoutes: RouteObject[] = [
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <Outlet />
      </ProtectedRoute>
    ),
    children: gamePages,
  },
];
