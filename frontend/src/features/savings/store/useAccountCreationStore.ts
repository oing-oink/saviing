import { create } from 'zustand';
import type { AccountCreationStep } from '@/features/savings/constants/accountCreationSteps';
import type { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';

/**
 * 기본 공통 데이터 (모든 계좌 타입에 필요)
 */
interface BaseFormData {
  /** 사용자 이름 */
  name: string;
  /** 생년월일 (YYYY-MM-DD 형식) */
  birth: string;
  /** 휴대폰 번호 */
  phone: string;
  /** 본인 인증 방법 */
  authMethod: string;
  /** 약관 동의 상태 */
  terms: {
    /** 서비스 이용약관 동의 */
    service: boolean;
    /** 개인정보 처리방침 동의 */
    privacy: boolean;
    /** 마케팅 정보 수신 동의 */
    marketing: boolean;
  };
}

/**
 * 적금 전용 필수 데이터
 */
interface SavingsFormData extends BaseFormData {
  /** 계좌 타입 (적금) */
  productType: typeof ACCOUNT_TYPES.SAVINGS;
  /** 월 납입금액 */
  depositAmount: number;
  /** 적금 기간 (개월) */
  period: number;
  /** 자동이체 날짜 */
  transferDate: string;
  /** 자동이체 계좌 */
  autoAccount: string;
  /** 자동이체 주기 */
  transferCycle: 'WEEKLY' | 'MONTHLY';
  /** 출금 계좌 ID */
  withdrawAccountId: number;
}

/**
 * 입출금 통장 전용 데이터
 */
interface CheckingFormData extends BaseFormData {
  /** 계좌 타입 (입출금통장) */
  productType: typeof ACCOUNT_TYPES.CHECKING;
}

/**
 * 초기 상태 (아직 타입 미선택 시)
 */
interface InitialFormData {
  /** 사용자 이름 */
  name: string;
  /** 생년월일 (YYYY-MM-DD 형식) */
  birth: string;
  /** 휴대폰 번호 */
  phone: string;
  /** 계좌 타입 (미선택 시 null) */
  productType:
    | typeof ACCOUNT_TYPES.SAVINGS
    | typeof ACCOUNT_TYPES.CHECKING
    | null;
  /** 본인 인증 방법 */
  authMethod: string;
  /** 약관 동의 상태 */
  terms: {
    service: boolean;
    privacy: boolean;
    marketing: boolean;
  };
  /** 월 납입금액 (적금 선택 시에만 필수) */
  depositAmount?: number;
  /** 적금 기간 (적금 선택 시에만 필수) */
  period?: number;
  /** 자동이체 날짜 (적금 선택 시에만 필수) */
  transferDate?: string;
  /** 자동이체 계좌 (적금 선택 시에만 필수) */
  autoAccount?: string;
  /** 자동이체 주기 (적금 선택 시에만 필수) */
  transferCycle?: 'WEEKLY' | 'MONTHLY';
  /** 출금 계좌 ID (적금 선택 시에만 필수) */
  withdrawAccountId?: number;
}

/**
 * 계좌 생성 폼 데이터 타입
 */
export type AccountCreationFormData =
  | InitialFormData // 타입 미선택 또는 진행 중
  | SavingsFormData // 적금 완료 상태
  | CheckingFormData; // 입출금 완료 상태

/**
 * 계좌 생성 스토어 상태 인터페이스
 */
interface AccountCreationState {
  /** 현재 진행 중인 단계 */
  step: AccountCreationStep;
  /** 폼 데이터 */
  form: AccountCreationFormData;
  /** 단계 설정 함수 */
  setStep: (step: AccountCreationStep) => void;
  /** 폼 데이터 업데이트 함수 */
  setForm: (data: Partial<AccountCreationFormData>) => void;
}

/**
 * 계좌 생성 프로세스의 전역 상태를 관리하는 Zustand 스토어
 *
 * 다음 기능들을 제공합니다:
 * - 현재 진행 단계 관리
 * - 사용자 입력 폼 데이터 관리
 * - 계좌 타입별 조건부 필드 관리
 *
 * @example
 * ```tsx
 * const { step, form, setStep, setForm } = useAccountCreationStore();
 *
 * // 다음 단계로 이동
 * setStep('USER_INFO');
 *
 * // 폼 데이터 업데이트
 * setForm({ name: '홍길동', phone: '010-1234-5678' });
 * ```
 */
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
