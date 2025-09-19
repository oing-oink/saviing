import { createBrowserRouter, Outlet } from 'react-router-dom';
import { authRoutes } from './authRoutes';
import { savingsRoutes } from './savingsRoutes';
import { gameRoutes } from './gameRoutes';
import { devRoutes, fallbackRoutes } from './devRoutes';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Outlet />,
    children: [
      ...authRoutes,
      ...savingsRoutes,
      ...gameRoutes,
      ...devRoutes,
      ...fallbackRoutes,
    ],
  },
]);
