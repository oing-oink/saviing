import { create } from 'zustand';
import type { LoginResponse, Customer } from '@/features/auth/types/authTypes';

interface CustomerStoreState {
  // 사용자 정보
  customer: Customer | null;
  // 인증 정보
  accessToken: string | null;
  customerId: number | null;
  expiresIn: number | null;
  isAuthenticated: boolean;

  // 액션들
  setLoginData: (loginData: LoginResponse) => void;
  setCustomerInfo: (customer: Customer) => void;
  clearAuth: () => void;
  loadFromSession: () => void;
}

const STORAGE_KEYS = {
  ACCESS_TOKEN: 'saviing_access_token',
  CUSTOMER_ID: 'saviing_customer_id',
  EXPIRES_IN: 'saviing_expires_in',
} as const;

/**
 * Customer 정보와 인증 상태를 관리하는 Zustand 스토어
 */
export const useCustomerStore = create<CustomerStoreState>((set, _) => ({
  // 초기 상태
  customer: null,
  accessToken: null,
  customerId: null,
  expiresIn: null,
  isAuthenticated: false,

  // 로그인 데이터 설정 (세션스토리지에도 저장)
  setLoginData: (loginData: LoginResponse) => {
    // 세션스토리지에 저장
    sessionStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, loginData.accessToken);
    sessionStorage.setItem(
      STORAGE_KEYS.CUSTOMER_ID,
      loginData.customerId.toString(),
    );
    sessionStorage.setItem(
      STORAGE_KEYS.EXPIRES_IN,
      loginData.expiresIn.toString(),
    );

    // 스토어 업데이트
    set({
      accessToken: loginData.accessToken,
      customerId: loginData.customerId,
      expiresIn: loginData.expiresIn,
      isAuthenticated: true,
    });
  },

  // 고객 정보 설정
  setCustomerInfo: (customer: Customer) => {
    set({ customer });
  },

  // 인증 정보 초기화
  clearAuth: () => {
    // 세션스토리지 클리어
    sessionStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
    sessionStorage.removeItem(STORAGE_KEYS.CUSTOMER_ID);
    sessionStorage.removeItem(STORAGE_KEYS.EXPIRES_IN);

    // 스토어 초기화
    set({
      customer: null,
      accessToken: null,
      customerId: null,
      expiresIn: null,
      isAuthenticated: false,
    });
  },

  // 세션스토리지에서 데이터 로드 (임시)
  loadFromSession: () => {
    const accessToken = sessionStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
    const customerId = sessionStorage.getItem(STORAGE_KEYS.CUSTOMER_ID);
    const expiresIn = sessionStorage.getItem(STORAGE_KEYS.EXPIRES_IN);

    if (accessToken && customerId && expiresIn) {
      set({
        accessToken,
        customerId: parseInt(customerId, 10),
        expiresIn: parseInt(expiresIn, 10),
        isAuthenticated: true,
      });
    }
  },
}));
