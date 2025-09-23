import { useSearchParams, useNavigate, useParams } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

type SavingsTerminationStep =
  | 'WARNING'
  | 'AUTH'
  | 'REASON'
  | 'CONFIRM'
  | 'COMPLETE';

export const useSavingsTermination = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { accountId } = useParams<{ accountId: string }>();

  const currentStepParam = searchParams.get('step') || 'WARNING';
  const fromParam = searchParams.get('from');

  const steps: SavingsTerminationStep[] = [
    'WARNING',
    'AUTH',
    'REASON',
    'CONFIRM',
    'COMPLETE',
  ];

  const currentStepIndex = steps.findIndex(step => step === currentStepParam);
  const currentStep = currentStepIndex >= 0 ? currentStepIndex + 1 : 1;
  const totalSteps = steps.length;

  const goToNextStep = () => {
    if (!accountId) {
      return;
    }

    const nextStepIndex = currentStepIndex + 1;
    if (nextStepIndex < steps.length) {
      const params = new URLSearchParams();
      params.set('step', steps[nextStepIndex]);
      if (fromParam) {
        params.set('from', fromParam);
      }

      navigate(
        `${PAGE_PATH.SAVINGS_TERMINATION_WITH_ID.replace(':accountId', accountId)}?${params.toString()}`,
      );
    }
  };

  const goToPrevStep = () => {
    if (!accountId) {
      return;
    }

    const prevStepIndex = currentStepIndex - 1;
    if (prevStepIndex >= 0) {
      const params = new URLSearchParams();
      params.set('step', steps[prevStepIndex]);
      if (fromParam) {
        params.set('from', fromParam);
      }

      navigate(
        `${PAGE_PATH.SAVINGS_TERMINATION_WITH_ID.replace(':accountId', accountId)}?${params.toString()}`,
      );
    } else {
      // 첫 번째 스텝에서 뒤로가기 시 상세 페이지로 돌아감
      navigate(
        `${PAGE_PATH.SAVINGS_DETAIL_WITH_ID.replace(':accountId', accountId)}`,
      );
    }
  };

  return {
    currentStep,
    totalSteps,
    currentStepParam: currentStepParam as SavingsTerminationStep,
    goToNextStep,
    goToPrevStep,
  };
};
