import type { AxiosRequestConfig } from "axios";
import { api } from "./axiosClient";
import type { ApiSuccessResponse } from "@/shared/types/api";

/**
 * 공통 요청 함수
 * 성공 시 ApiSuccessResponse<T> 반환
 * 실패 시 인터셉터에서 ApiError로 변환되어 throw됨
 *
 * @template T - body 타입
 * @param cfg - Axios 요청 설정
 * @returns ApiSuccessResponse<T>
 */
const request = async <T>(
  cfg: AxiosRequestConfig
): Promise<ApiSuccessResponse<T>> => {
  const { data } = await api.request<ApiSuccessResponse<T>>(cfg);
  return data;
};

/**
 * HTTP 메서드 래퍼
 * 호출부에서 api.request를 직접 쓰지 않고 의도가 드러나는 메서드로 감싼다.
 */
export const http = {
  /**
   * GET 요청
   * @template T - body 타입
   * @param url - URL
   * @param config - 추가 Axios 설정
   */
  get: <T>(url: string, config?: AxiosRequestConfig) =>
    request<T>({ url, method: "GET", ...config }),

  /**
   * POST 요청
   * @template T - body 타입
   * @param url - URL
   * @param body - 본문
   * @param config - 추가 Axios 설정
   */
  post: <T>(url: string, body?: unknown, config?: AxiosRequestConfig) =>
    request<T>({ url, method: "POST", data: body, ...config }),

  /**
   * PUT 요청
   * @template T - body 타입
   * @param url - URL
   * @param body - 본문
   * @param config - 추가 Axios 설정
   */
  put: <T>(url: string, body?: unknown, config?: AxiosRequestConfig) =>
    request<T>({ url, method: "PUT", data: body, ...config }),

  /**
   * DELETE 요청
   * @template T - body 타입
   * @param url - URL
   * @param config - 추가 Axios 설정
   */
  delete: <T>(url: string, config?: AxiosRequestConfig) =>
    request<T>({ url, method: "DELETE", ...config }),
};
