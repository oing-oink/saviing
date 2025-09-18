import { api } from '@/shared/services/api/axiosClient';
import { API_ENDPOINTS } from '@/shared/constants/apiEndpoints';
import type { LoginResponse } from '../types/authTypes';

/**
 * 구글 OAuth 코드로 로그인
 * @param code - OAuth 인증 코드
 * @returns 로그인 성공 시 토큰 정보
 */
export const loginWithGoogleCode = async (
  code: string,
): Promise<LoginResponse> => {
  const response = await api.post<{
    success: boolean;
    status: number;
    body: LoginResponse;
  }>(API_ENDPOINTS.AUTH.LOGIN, null, {
    params: { code },
  });

  return response.data.body;
};

/**
 * 구글 OAuth URL 생성
 * @returns 구글 OAuth 로그인 URL
 */
export const getGoogleOAuthUrl = (): string => {
  const clientId = import.meta.env.VITE_OAUTH2_GOOGLE_CLIENT_ID;
  const redirectUri = import.meta.env.VITE_OAUTH2_GOOGLE_REDIRECT_URI;

  const params = new URLSearchParams({
    client_id: clientId,
    redirect_uri: redirectUri,
    response_type: 'code',
    scope: 'openid email profile',
  });

  return `https://accounts.google.com/o/oauth2/v2/auth?${params.toString()}`;
};
