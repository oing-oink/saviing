import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
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
}

/**
 * Customer 정보와 인증 상태를 관리하는 Zustand 스토어
 */
export const useCustomerStore = create<CustomerStoreState>()(
  persist(
    (set, _) => ({
      // 초기 상태
      customer: null,
      accessToken: null,
      customerId: null,
      expiresIn: null,
      isAuthenticated: false,

      // 로그인 데이터 설정
      setLoginData: (loginData: LoginResponse) => {
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
        set({
          customer: null,
          accessToken: null,
          customerId: null,
          expiresIn: null,
          isAuthenticated: false,
        });
      },
    }),
    {
      name: 'saviing-customer-store',
      storage: createJSONStorage(() => sessionStorage),
    },
  ),
);

// store 인스턴스를 loader에서 사용할 수 있도록 export
export const customerStore = useCustomerStore;
