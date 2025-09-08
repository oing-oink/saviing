import type { AxiosError } from 'axios';

/**
 * API 성공 응답
 * @template T - body 데이터 타입
 */
export interface ApiSuccessResponse<T> {
  success: true;
  status: number;
  body?: T;
}

/**
 * API 유효성 검증 에러 파라미터
 */
export interface InvalidParam {
  field: string;
  message: string;
  rejectedValue: string;
}

/**
 * API 에러 응답
 */
export interface ApiErrorResponse {
  success: false;
  status: number;
  code: string;
  message: string;
  timestamp: string;
  invalidParams?: InvalidParam[];
}

/**
 * API 에러 래퍼
 * AxiosError를 프로젝트 공통 에러로 변환해 사용성을 높인다.
 */
export class ApiError extends Error {
  /** 원본 Axios 에러 객체 */
  public readonly axiosError: AxiosError;
  /** 서버가 내려준 공통 에러 응답 */
  public readonly response?: ApiErrorResponse;

  /**
   * @param err - 원본 AxiosError
   */
  constructor(err: AxiosError) {
    super(err.message);
    this.name = 'ApiError';
    this.axiosError = err;

    // 서버가 공통 에러 바디를 내려주면 타입 단정
    if (err.response?.data) {
      this.response = err.response.data as ApiErrorResponse;
    }
  }
}
