import axios from 'axios';
import { onRequest, onResponseError } from './interceptors';
import { API_BASE_URLS } from '@/shared/constants/apiEndpoints';

/**
 * Bank API용 Axios 인스턴스
 */
export const bankApi = axios.create({
  baseURL: API_BASE_URLS.BANK,
  timeout: 10_000,
  withCredentials: true,
});

/**
 * Game API용 Axios 인스턴스
 */
export const gameApi = axios.create({
  baseURL: API_BASE_URLS.GAME,
  timeout: 10_000,
  withCredentials: true,
});

// 인터셉터 연결
bankApi.interceptors.request.use(onRequest);
bankApi.interceptors.response.use(res => res, onResponseError);

gameApi.interceptors.request.use(onRequest);
gameApi.interceptors.response.use(res => res, onResponseError);
