import axios from "axios";
import { onRequest, onResponseError } from "./interceptors";

/**
 * 공용 Axios 인스턴스
 * baseURL, timeout, withCredentials 등 전역 설정을 적용한다.
 */
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10_000,
  withCredentials: true,
});

// 인터셉터 연결
api.interceptors.request.use(onRequest);
api.interceptors.response.use((res) => res, onResponseError);
