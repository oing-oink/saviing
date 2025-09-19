import type { RouteObject } from 'react-router-dom';
import { redirect } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import LoginPage from '@/pages/LoginPage';
import OnboardingPage from '@/pages/OnboardingPage';
import AuthCallbackPage from '@/pages/AuthCallbackPage';
import { loginWithGoogleCode } from '@/features/auth/api/authApi';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';

const oauthCallbackLoader = async ({ request }: { request: Request }) => {
  const url = new URL(request.url);
  const code = url.searchParams.get('code');
  const error = url.searchParams.get('error');

  if (error) {
    throw new Error('Google 로그인이 취소되었습니다.');
  }

  if (!code) {
    throw new Error('인증 코드를 찾을 수 없습니다.');
  }

  try {
    const loginData = await loginWithGoogleCode(code);
    useCustomerStore.getState().setLoginData(loginData);
    return redirect('/');
  } catch (error) {
    console.error('로그인 실패:', error);
    throw new Error('로그인 처리 중 오류가 발생했습니다.');
  }
};

export const authRoutes: RouteObject[] = [
  { path: PAGE_PATH.LOGIN, element: <LoginPage /> },
  { path: PAGE_PATH.ONBOARDING, element: <OnboardingPage /> },
  {
    path: PAGE_PATH.AUTH_CALLBACK,
    element: <AuthCallbackPage />,
    loader: oauthCallbackLoader,
  },
];
