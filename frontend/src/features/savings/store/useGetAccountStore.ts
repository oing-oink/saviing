import { create } from 'zustand';
import type { GetAccountStep } from '@/features/savings/constants/getAccountSteps';
import type { AccountType } from '@/features/savings/constants/accountTypes';

export interface GetAccountFormData {
  name?: string;
  birth?: string;
  phone?: string;
  productType?: AccountType;
  depositAmount?: number;
  authMethod?: string;
  period?: number;
  terms?: {
    service: boolean; // 필수
    privacy: boolean; // 필수
    marketing?: boolean; // 선택
  };
  transferDate?: string;
  autoAccount?: string;
}

interface GetAccountState {
  step: GetAccountStep;
  form: GetAccountFormData;
  setStep: (step: GetAccountStep) => void;
  setForm: (data: Partial<GetAccountFormData>) => void;
}

export const useGetAccountStore = create<GetAccountState>(set => ({
  step: 'START',
  form: {},
  setStep: (step: GetAccountStep) => set({ step }),
  setForm: (data: Partial<GetAccountFormData>) =>
    set(state => ({ form: { ...state.form, ...data } })),
}));
