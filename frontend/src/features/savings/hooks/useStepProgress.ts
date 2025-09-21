import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import {
  SAVINGS_STEPS,
  CHECKING_STEPS,
  type AccountCreationStep,
} from '@/features/savings/constants/accountCreationSteps';
import { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';

/**
 * 계좌 생성 funnel의 단계 진행을 관리하는 커스텀 훅
 * URL 파라미터 기반으로 현재 단계를 결정하고 단계 간 이동을 처리합니다.
 *
 * @returns {object} 단계 진행 관련 상태와 함수들
 * @returns {number} currentStep - 현재 단계 번호 (1부터 시작)
 * @returns {number} totalSteps - 전체 단계 수
 * @returns {boolean} canGoBack - 이전 단계로 이동 가능 여부
 * @returns {AccountCreationStep} currentStepFromUrl - URL 파라미터에서 가져온 현재 단계
 * @returns {function} goToPreviousStep - 이전 단계로 이동하는 함수
 * @returns {function} goToNextStep - 다음 단계로 이동하는 함수
 * @returns {function} goToStep - 특정 단계로 이동하는 함수
 */
export const useStepProgress = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { form } = useAccountCreationStore();

  // 현재 URL 파라미터에서 step 결정
  const currentStepFromUrl =
    (searchParams.get('step') as AccountCreationStep) || 'START';

  // 계좌 타입에 따라 다른 스텝 배열 사용
  const isChecking = form.productType === ACCOUNT_TYPES.CHECKING;
  const currentSteps = isChecking ? CHECKING_STEPS : SAVINGS_STEPS;

  // 안전한 indexOf 사용
  const currentIndex = currentSteps.findIndex(s => s === currentStepFromUrl);
  const totalSteps = currentSteps.length;

  /**
   * 현재 URL 파라미터를 포함한 경로 생성
   */
  const createPathWithParams = (path: string) => {
    const currentParams = searchParams.toString();
    return currentParams ? `${path}?${currentParams}` : path;
  };

  /**
   * 이전 단계로 이동
   * 현재 단계가 첫 번째가 아닌 경우에만 동작
   */
  const goToPreviousStep = () => {
    if (currentIndex > 0) {
      const prevStep = currentSteps[currentIndex - 1];
      if (prevStep) {
        navigate(createPathWithParams(prevPath));
      }
    }
  };

  /**
   * 다음 단계로 이동
   * 현재 단계가 마지막이 아닌 경우에만 동작
   */
  const goToNextStep = () => {
    if (currentIndex < currentSteps.length - 1) {
      const nextStep = currentSteps[currentIndex + 1];
      if (nextStep) {
        navigate(createPathWithParams(nextPath));
      }
    }
  };

  /**
   * 특정 단계로 직접 이동
   * @param {AccountCreationStep} targetStep - 이동할 목표 단계
   */
  const goToStep = (targetStep: AccountCreationStep) => {
    const targetPath = STEP_TO_PATH[targetStep];
    navigate(createPathWithParams(targetPath));
  };

  return {
    currentStep: Math.max(currentIndex + 1, 1),
    totalSteps,
    canGoBack: currentIndex > 0,
    currentStepFromUrl,
    goToPreviousStep,
    goToNextStep,
    goToStep,
  };
};
