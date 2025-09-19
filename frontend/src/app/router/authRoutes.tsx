import type { RouteObject } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import LoginPage from '@/pages/LoginPage';
import OnboardingPage from '@/pages/OnboardingPage';
import AuthCallbackPage from '@/pages/AuthCallbackPage';

// 인증 관련 공개 페이지들 (로그인 불필요)
const publicAuthPages = [
  { path: PAGE_PATH.LOGIN, element: <LoginPage /> },
  { path: PAGE_PATH.ONBOARDING, element: <OnboardingPage /> },
  { path: PAGE_PATH.AUTH_CALLBACK, element: <AuthCallbackPage /> },
];

export const authRoutes: RouteObject[] = publicAuthPages;
