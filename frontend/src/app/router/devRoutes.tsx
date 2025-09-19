import type { RouteObject } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import ColorTestPage from '@/pages/ColorTestPage';
import NotFoundPage from '@/pages/NotFoundPage';

export const devRoutes: RouteObject[] = [
  { path: PAGE_PATH.COLORTEST, element: <ColorTestPage /> },
];

export const fallbackRoutes: RouteObject[] = [
  { path: '*', element: <NotFoundPage /> },
];
