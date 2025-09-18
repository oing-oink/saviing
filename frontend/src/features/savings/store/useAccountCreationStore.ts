import { create } from 'zustand';
import type { AccountCreationStep } from '@/features/savings/constants/accountCreationSteps';
import type { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';

// 기본 공통 데이터 (모든 계좌 타입에 필요)
interface BaseFormData {
  name: string;
  birth: string;
  phone: string;
  authMethod: string;
  terms: {
    service: boolean;
    privacy: boolean;
    marketing: boolean;
  };
}

// 적금 전용 필수 데이터
interface SavingsFormData extends BaseFormData {
  productType: typeof ACCOUNT_TYPES.SAVINGS;
  depositAmount: number;
  period: number;
  transferDate: string;
  autoAccount: string;
}

// 입출금 통장 전용 데이터
interface CheckingFormData extends BaseFormData {
  productType: typeof ACCOUNT_TYPES.CHECKING;
}

// 초기 상태 (아직 타입 미선택 시)
interface InitialFormData {
  name: string;
  birth: string;
  phone: string;
  productType:
    | typeof ACCOUNT_TYPES.SAVINGS
    | typeof ACCOUNT_TYPES.CHECKING
    | null;
  authMethod: string;
  terms: {
    service: boolean;
    privacy: boolean;
    marketing: boolean;
  };
  // 적금 관련은 optional (초기 상태에서)
  depositAmount?: number;
  period?: number;
  transferDate?: string;
  autoAccount?: string;
}

export type AccountCreationFormData =
  | InitialFormData // 타입 미선택 또는 진행 중
  | SavingsFormData // 적금 완료 상태
  | CheckingFormData; // 입출금 완료 상태

interface AccountCreationState {
  step: AccountCreationStep;
  form: AccountCreationFormData;
  setStep: (step: AccountCreationStep) => void;
  setForm: (data: Partial<AccountCreationFormData>) => void;
}

export const useAccountCreationStore = create<AccountCreationState>(set => ({
  step: 'START',
  form: {
    name: '',
    birth: '',
    phone: '',
    productType: null,
    authMethod: '',
    terms: {
      service: false,
      privacy: false,
      marketing: false,
    },
  },
  setStep: (step: AccountCreationStep) => set({ step }),
  setForm: (data: Partial<AccountCreationFormData>) =>
    set(state => ({ form: { ...state.form, ...data } })),
}));
