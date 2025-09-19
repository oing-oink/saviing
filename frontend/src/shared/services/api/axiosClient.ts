import axios from 'axios';
import { onRequest, onResponseError } from './interceptors';
import { API_BASE_URLS } from '@/shared/constants/apiEndpoints';

/**
 * 통합 API 클라이언트
 * URL prefix에 따라 baseURL을 동적으로 설정
 */
export const api = axios.create({
  timeout: 10_000,
  withCredentials: true,
});

// baseURL 동적 설정 인터셉터
api.interceptors.request.use(config => {
  if (config.url?.includes('/game')) {
    config.baseURL = API_BASE_URLS.GAME;
  } else {
    config.baseURL = API_BASE_URLS.BANK;
  }
  return config;
});

// 기존 인터셉터 연결
api.interceptors.request.use(onRequest);
api.interceptors.response.use(res => res, onResponseError);
