import type { RouteObject } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import LoginPage from '@/pages/LoginPage';
import OnboardingPage from '@/pages/OnboardingPage';
import AuthCallbackPage from '@/pages/AuthCallbackPage';

export const authRoutes: RouteObject[] = [
  { path: PAGE_PATH.LOGIN, element: <LoginPage /> },
  { path: PAGE_PATH.ONBOARDING, element: <OnboardingPage /> },
  { path: PAGE_PATH.AUTH_CALLBACK, element: <AuthCallbackPage /> },
];
