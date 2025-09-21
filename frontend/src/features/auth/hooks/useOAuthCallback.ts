import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { loginWithGoogleCode } from '@/features/auth/api/authApi';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';

interface UseOAuthCallbackState {
  isLoading: boolean;
  error: string | null;
}

export const useOAuthCallback = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { setLoginData } = useCustomerStore();

  const [state, setState] = useState<UseOAuthCallbackState>({
    isLoading: false,
    error: null,
  });

  useEffect(() => {
    const handleCallback = async () => {
      const code = searchParams.get('code');
      const error = searchParams.get('error');

      // OAuth 에러가 있는 경우
      if (error) {
        setState({
          isLoading: false,
          error: 'Google 로그인이 취소되었습니다.',
        });
        return;
      }

      // 코드가 없는 경우
      if (!code) {
        setState({
          isLoading: false,
          error: '인증 코드를 찾을 수 없습니다.',
        });
        return;
      }

      // 로그인 처리
      try {
        setState({ isLoading: true, error: null });

        const loginData = await loginWithGoogleCode(code);

        // Customer 스토어에 로그인 데이터 저장 (세션스토리지에도 자동 저장됨)
        setLoginData(loginData);

        // 홈페이지로 리다이렉트
        navigate('/');
      } catch (error) {
        console.error('로그인 실패:', error);
        setState({
          isLoading: false,
          error: '로그인 처리 중 오류가 발생했습니다.',
        });
      }
    };

    handleCallback();
  }, [searchParams, navigate, setLoginData]);

  return state;
};
