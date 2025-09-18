import { useGetAccountStore } from '@/features/savings/store/useGetAccountStore';
import {
  SAVINGS_STEPS,
  CHECKING_STEPS,
} from '@/features/savings/constants/getAccountSteps';
import { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';

export const useStepProgress = () => {
  const { step, setStep, form } = useGetAccountStore();

  // 계좌 타입에 따라 다른 스텝 배열 사용
  const isChecking = form.productType === ACCOUNT_TYPES.CHECKING;

  const currentSteps = isChecking ? CHECKING_STEPS : SAVINGS_STEPS;

  // 안전한 indexOf 사용 (타입 단언 없이)
  const currentIndex = currentSteps.findIndex(s => s === step);
  const totalSteps = currentSteps.length;

  const goToPreviousStep = () => {
    if (currentIndex > 0) {
      const prevStep = currentSteps[currentIndex - 1];
      if (prevStep) {
        setStep(prevStep);
      }
    }
  };

  const goToNextStep = () => {
    if (currentIndex < currentSteps.length - 1) {
      const nextStep = currentSteps[currentIndex + 1];
      if (nextStep) {
        setStep(nextStep);
      }
    }
  };

  return {
    currentStep: Math.max(currentIndex + 1, 1),
    totalSteps,
    canGoBack: currentIndex > 0,
    goToPreviousStep,
    goToNextStep,
  };
};
