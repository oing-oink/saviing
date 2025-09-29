import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import {
  SAVINGS_STEPS,
  CHECKING_STEPS,
  type AccountCreationStep,
} from '@/features/savings/constants/accountCreationSteps';
import { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';
import {
  ACCOUNT_CREATION_STEPS_PATH,
  PAGE_PATH,
} from '@/shared/constants/path';

/**
 * 계좌 생성 funnel의 단계 진행을 관리하는 커스텀 훅
 * URL 파라미터 기반으로 현재 단계를 결정하고 단계 간 이동을 처리합니다.
 * from 파라미터를 통해 입장한 페이지를 기억하고, funnel 종료 시 해당 페이지로 돌아갑니다.
 *
 * @returns {object} 단계 진행 관련 상태와 함수들
 * @returns {number} currentStep - 현재 단계 번호 (1부터 시작)
 * @returns {number} totalSteps - 전체 단계 수
 * @returns {boolean} canGoBack - 이전 단계로 이동 가능 여부
 * @returns {AccountCreationStep} currentStepFromUrl - URL 파라미터에서 가져온 현재 단계
 * @returns {function} goToPreviousStep - 이전 단계로 이동하는 함수 (첫 단계에서는 입장 페이지로)
 * @returns {function} goToNextStep - 다음 단계로 이동하는 함수 (완료 단계에서는 입장 페이지로)
 * @returns {function} goToStep - 특정 단계로 이동하는 함수
 * @returns {function} exitFunnel - funnel을 종료하고 입장한 원래 페이지로 돌아가는 함수
 */
export const useStepProgress = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { form } = useAccountCreationStore();

  // 현재 URL 파라미터에서 step 결정
  const currentStepFromUrl =
    (searchParams.get('step') as AccountCreationStep) || 'START';

  // URL 파라미터에서 type을 가져와서 우선 적용, 없으면 store의 productType 사용
  const urlProductType = searchParams.get('type') as
    | typeof ACCOUNT_TYPES.SAVINGS
    | typeof ACCOUNT_TYPES.CHECKING
    | null;
  const productType = urlProductType || form.productType;

  // 계좌 타입에 따라 다른 스텝 배열 사용
  const isChecking = productType === ACCOUNT_TYPES.CHECKING;
  const currentSteps = isChecking ? CHECKING_STEPS : SAVINGS_STEPS;

  // 안전한 indexOf 사용
  const currentIndex = currentSteps.findIndex(s => s === currentStepFromUrl);
  const totalSteps = currentSteps.length;

  // Step to Path 매핑
  const STEP_TO_PATH = ACCOUNT_CREATION_STEPS_PATH;

  // 이전/다음 스텝의 경로 계산
  const prevStep = currentIndex > 0 ? currentSteps[currentIndex - 1] : null;
  const nextStep =
    currentIndex < currentSteps.length - 1
      ? currentSteps[currentIndex + 1]
      : null;
  const prevPath = prevStep ? STEP_TO_PATH[prevStep] : null;
  const nextPath = nextStep ? STEP_TO_PATH[nextStep] : null;

  /**
   * 입장한 페이지 감지 (from 파라미터 기반)
   * funnel 진입 시 설정된 from 파라미터를 통해 원래 페이지를 결정
   */
  const getSourcePage = () => {
    const from = searchParams.get('from');
    switch (from) {
      case 'home':
        return PAGE_PATH.HOME;
      case 'wallet':
        return PAGE_PATH.WALLET;
      case 'products':
        return PAGE_PATH.PRODUCTS;
      default:
        return PAGE_PATH.WALLET; // 기본값: from 파라미터가 없으면 wallet으로
    }
  };

  /**
   * 현재 URL 파라미터를 포함한 경로 생성
   */
  const createPathWithParams = (path: string) => {
    const currentParams = searchParams.toString();
    return currentParams ? `${path}?${currentParams}` : path;
  };

  /**
   * 이전 단계로 이동
   * 첫 번째 단계인 경우 입장한 원래 페이지로 돌아감
   */
  const goToPreviousStep = () => {
    if (currentIndex > 0 && prevPath) {
      navigate(createPathWithParams(prevPath));
    } else {
      // 첫 번째 단계인 경우 입장한 원래 페이지로 돌아가기
      navigate(getSourcePage());
    }
  };

  /**
   * 다음 단계로 이동
   * 마지막 단계인 경우 완료 후 입장한 원래 페이지로 돌아감
   */
  const goToNextStep = () => {
    if (currentIndex < currentSteps.length - 1 && nextPath) {
      navigate(createPathWithParams(nextPath));
    } else if (currentStepFromUrl === 'COMPLETE') {
      // 완료 단계에서는 입장한 원래 페이지로 돌아가기
      navigate(getSourcePage());
    }
  };

  /**
   * funnel을 종료하고 입장한 원래 페이지로 돌아가기
   */
  const exitFunnel = () => {
    navigate(getSourcePage());
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
    exitFunnel,
  };
};
