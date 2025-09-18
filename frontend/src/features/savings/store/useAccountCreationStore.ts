import { create } from 'zustand';
import type { AccountCreationStep } from '@/features/savings/constants/accountCreationSteps';
import type { AccountType } from '@/features/savings/constants/accountTypes';

export interface AccountCreationFormData {
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

interface AccountCreationState {
  step: AccountCreationStep;
  form: AccountCreationFormData;
  setStep: (step: AccountCreationStep) => void;
  setForm: (data: Partial<AccountCreationFormData>) => void;
}

export const useAccountCreationStore = create<AccountCreationState>(set => ({
  step: 'START',
  form: {},
  setStep: (step: AccountCreationStep) => set({ step }),
  setForm: (data: Partial<AccountCreationFormData>) =>
    set(state => ({ form: { ...state.form, ...data } })),
}));
