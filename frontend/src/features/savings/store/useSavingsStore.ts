import { create } from 'zustand';

/**
 * 현재 적금 계좌 상태 인터페이스
 */
interface SavingsState {
  /** 현재 선택된 적금 계좌 ID */
  currentAccountId: number | null;
  /** 현재 선택된 적금 계좌 번호 (표시용) */
  currentAccountNumber?: string;
  /** 현재 적금 계좌 설정 함수 */
  setCurrentAccount: (accountId: number, accountNumber?: string) => void;
  /** 현재 적금 계좌 정보 초기화 함수 */
  clearCurrentAccount: () => void;
}

/**
 * 적금 관련 전역 상태를 관리하는 Zustand 스토어
 *
 * 현재 선택된 적금 계좌의 최소한의 정보만 저장합니다:
 * - accountId: URL 파라미터 및 API 호출용
 * - accountNumber: 화면 표시용 (선택적)
 *
 * 상세 데이터는 각 페이지에서 accountId로 API 호출하여 가져옵니다.
 */
export const useSavingsStore = create<SavingsState>(set => ({
  currentAccountId: null,
  currentAccountNumber: undefined,

  setCurrentAccount: (accountId: number, accountNumber?: string) =>
    set({
      currentAccountId: accountId,
      currentAccountNumber: accountNumber,
    }),

  clearCurrentAccount: () =>
    set({
      currentAccountId: null,
      currentAccountNumber: undefined,
    }),
}));
