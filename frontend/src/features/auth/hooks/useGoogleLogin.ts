import { useCallback } from 'react';
import { getGoogleOAuthUrl } from '../api/authApi';

export const useGoogleLogin = () => {
  const startGoogleLogin = useCallback(() => {
    const oauthUrl = getGoogleOAuthUrl();
    // 현재 창에서 구글 로그인 페이지로 이동
    window.location.href = oauthUrl;
  }, []);

  return {
    startGoogleLogin,
  };
};