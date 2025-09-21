import { create } from 'zustand';

/**
 * 적금 설정 변경 단계
 */
export type SavingsSettingsStep =
  | 'CURRENT_INFO'
  | 'SELECT_CHANGE'
  | 'NEW_SETTINGS'
  | 'IMPACT_REVIEW'
  | 'CONFIRM'
  | 'COMPLETE';

/**
 * 변경 가능한 설정 타입
 */
export type ChangeType = 'AMOUNT' | 'TRANSFER_DATE' | 'AUTO_ACCOUNT';

/**
 * 현재 적금 정보 (API에서 가져온 데이터)
 */
interface CurrentSavingsInfo {
  /** 현재 월 납입금액 */
  currentAmount: number;
  /** 현재 자동이체 날짜 */
  currentTransferDate: string;
  /** 현재 누적 금액 */
  currentBalance: number;
  /** 현재 자동이체 연결 계좌 */
  currentAutoAccount: string;
}

/**
 * 새로운 설정값
 */
interface NewSettingsData {
  /** 새로운 월 납입금액 */
  newAmount?: number;
  /** 새로운 자동이체 날짜 */
  newTransferDate?: string;
  /** 새로운 자동이체 연결 계좌 */
  newAutoAccount?: string;
}

/**
 * 변경 영향 분석 결과
 */
interface ImpactAnalysis {
  /** 예상 최종 금액 변화 */
  finalAmountChange: number;
  /** 이자 수익 변화 */
  interestChange: number;
  /** 완료 예정일 변화 */
  completionDateChange: string;
  /** 월 부담금 변화 */
  monthlyBurdenChange: number;
}

/**
 * 적금 설정 변경 스토어 상태
 */
interface SavingsSettingsState {
  /** 현재 진행 단계 */
  step: SavingsSettingsStep;
  /** 선택된 변경 타입들 */
  selectedChangeTypes: ChangeType[];
  /** 현재 적금 정보 */
  currentInfo: CurrentSavingsInfo | null;
  /** 새로운 설정값 */
  newSettings: NewSettingsData;
  /** 변경 영향 분석 결과 */
  impactAnalysis: ImpactAnalysis | null;

  /** 단계 설정 */
  setStep: (step: SavingsSettingsStep) => void;
  /** 변경 타입 선택/해제 */
  toggleChangeType: (type: ChangeType) => void;
  /** 현재 적금 정보 설정 */
  setCurrentInfo: (info: CurrentSavingsInfo) => void;
  /** 새로운 설정값 업데이트 */
  updateNewSettings: (settings: Partial<NewSettingsData>) => void;
  /** 영향 분석 결과 설정 */
  setImpactAnalysis: (analysis: ImpactAnalysis) => void;
  /** 전체 상태 초기화 */
  reset: () => void;
}

/**
 * 적금 설정 변경 프로세스의 전역 상태를 관리하는 Zustand 스토어
 *
 * 기능:
 * - 설정 변경 과정의 단계별 진행 상태 관리
 * - 현재 적금 정보와 새로운 설정값 비교
 * - 변경 영향 분석 데이터 저장
 * - 선택된 변경 타입들 추적
 *
 * @example
 * ```tsx
 * const { step, selectedChangeTypes, setStep, toggleChangeType } = useSavingsSettingsStore();
 *
 * // 다음 단계로 이동
 * setStep('NEW_SETTINGS');
 *
 * // 변경 타입 선택
 * toggleChangeType('AMOUNT');
 * ```
 */
export const useSavingsSettingsStore = create<SavingsSettingsState>(set => ({
  step: 'CURRENT_INFO',
  selectedChangeTypes: [],
  currentInfo: null,
  newSettings: {},
  impactAnalysis: null,

  setStep: (step: SavingsSettingsStep) => set({ step }),

  toggleChangeType: (type: ChangeType) =>
    set(state => ({
      selectedChangeTypes: state.selectedChangeTypes.includes(type)
        ? state.selectedChangeTypes.filter(t => t !== type)
        : [...state.selectedChangeTypes, type],
    })),

  setCurrentInfo: (info: CurrentSavingsInfo) => set({ currentInfo: info }),

  updateNewSettings: (settings: Partial<NewSettingsData>) =>
    set(state => ({
      newSettings: { ...state.newSettings, ...settings },
    })),

  setImpactAnalysis: (analysis: ImpactAnalysis) =>
    set({ impactAnalysis: analysis }),

  reset: () =>
    set({
      step: 'CURRENT_INFO',
      selectedChangeTypes: [],
      currentInfo: null,
      newSettings: {},
      impactAnalysis: null,
    }),
}));
