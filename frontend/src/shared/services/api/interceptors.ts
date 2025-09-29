import { AxiosHeaders } from 'axios';
import type { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { ApiError } from '@/shared/types/api';
import { PAGE_PATH } from '@/shared/constants/path';
import { router } from '@/app/router/routes';
import { customerStore } from '@/features/auth/store/useCustomerStore';

/**
 * 요청 인터셉터
 *
 * 현재는 토큰을 주입하지 않는다.
 * @returns 변경 없이 그대로 요청 설정을 반환
 */
export const onRequest = (config: InternalAxiosRequestConfig) => {
  const { accessToken } = customerStore.getState();

  if (accessToken) {
    const headers =
      config.headers instanceof AxiosHeaders
        ? config.headers
        : new AxiosHeaders(config.headers);

    headers.set('Authorization', `Bearer ${accessToken}`);
    config.headers = headers;
  }

  return config;
};

/**
 * 응답 에러 인터셉터
 *
 * - 401이면 로그인 페이지로 이동한다.
 * - 모든 에러를 ApiError로 감싸서 throw한다.
 *
 * @param error - AxiosError
 * @returns ApiError를 reject
 */
// TODO: zustand 도입 후, 401 시 토큰 정리 추가
export const onResponseError = (error: AxiosError) => {
  if (error.response?.status === 401) {
    // TODO: zustand 도입 후 토큰 정리 추가
    router.navigate(PAGE_PATH.ONBOARDING);
  }
  return Promise.reject(new ApiError(error));
};
