import axios, { type AxiosRequestConfig } from "axios";
import { router } from "@/app/router/routes";
import { PAGE_PATH } from "../constants/path";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000, // 10초 안에 응답이 없으면 요청을 실패로 처리
  withCredentials: true, // 크로스 도메인 요청에도 쿠키/인증정보를 포함
  // 서버도 CORS 헤더에 Access-Control-Allow_Credentials: true 설정 필요
});

// 요청 인터셉터
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// 응답 인터셉터
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      router.navigate(PAGE_PATH.LOGIN);
    }
    return Promise.reject(err);
  }
);

// Axios 응답에서 data만 꺼내는 헬퍼 함수
export async function http<T>(cfg: AxiosRequestConfig): Promise<T> {
  const { data } = await api.request<T>(cfg);
  return data;
}
