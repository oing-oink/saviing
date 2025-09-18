import { useNavigate, useLocation } from 'react-router-dom';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import {
  SAVINGS_STEPS,
  CHECKING_STEPS,
  type AccountCreationStep,
} from '@/features/savings/constants/accountCreationSteps';
import { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';
import { ACCOUNT_CREATION_STEPS_PATH } from '@/shared/constants/path';

// Step to Path mapping
const STEP_TO_PATH: Record<AccountCreationStep, string> = {
  START: ACCOUNT_CREATION_STEPS_PATH.START,
  PRODUCT_TYPE: ACCOUNT_CREATION_STEPS_PATH.PRODUCT_TYPE,
  USER_INFO: ACCOUNT_CREATION_STEPS_PATH.USER_INFO,
  AUTH: ACCOUNT_CREATION_STEPS_PATH.AUTH,
  TERMS: ACCOUNT_CREATION_STEPS_PATH.TERMS,
  SET_CONDITION: ACCOUNT_CREATION_STEPS_PATH.SET_CONDITION,
  CONFIRM: ACCOUNT_CREATION_STEPS_PATH.CONFIRM,
  COMPLETE: ACCOUNT_CREATION_STEPS_PATH.COMPLETE,
};

// Path to Step mapping
const PATH_TO_STEP: Record<string, AccountCreationStep> = {
  [ACCOUNT_CREATION_STEPS_PATH.START]: 'START',
  [ACCOUNT_CREATION_STEPS_PATH.PRODUCT_TYPE]: 'PRODUCT_TYPE',
  [ACCOUNT_CREATION_STEPS_PATH.USER_INFO]: 'USER_INFO',
  [ACCOUNT_CREATION_STEPS_PATH.AUTH]: 'AUTH',
  [ACCOUNT_CREATION_STEPS_PATH.TERMS]: 'TERMS',
  [ACCOUNT_CREATION_STEPS_PATH.SET_CONDITION]: 'SET_CONDITION',
  [ACCOUNT_CREATION_STEPS_PATH.CONFIRM]: 'CONFIRM',
  [ACCOUNT_CREATION_STEPS_PATH.COMPLETE]: 'COMPLETE',
};

export const useStepProgress = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { form } = useAccountCreationStore();

  // 현재 URL에서 step 결정
  const currentStepFromUrl = PATH_TO_STEP[location.pathname] || 'START';

  // 계좌 타입에 따라 다른 스텝 배열 사용
  const isChecking = form.productType === ACCOUNT_TYPES.CHECKING;
  const currentSteps = isChecking ? CHECKING_STEPS : SAVINGS_STEPS;

  // 안전한 indexOf 사용
  const currentIndex = currentSteps.findIndex(s => s === currentStepFromUrl);
  const totalSteps = currentSteps.length;

  const goToPreviousStep = () => {
    if (currentIndex > 0) {
      const prevStep = currentSteps[currentIndex - 1];
      if (prevStep) {
        const prevPath = STEP_TO_PATH[prevStep];
        navigate(prevPath);
      }
    }
  };

  const goToNextStep = () => {
    if (currentIndex < currentSteps.length - 1) {
      const nextStep = currentSteps[currentIndex + 1];
      if (nextStep) {
        const nextPath = STEP_TO_PATH[nextStep];
        navigate(nextPath);
      }
    }
  };

  const goToStep = (targetStep: AccountCreationStep) => {
    const targetPath = STEP_TO_PATH[targetStep];
    navigate(targetPath);
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
