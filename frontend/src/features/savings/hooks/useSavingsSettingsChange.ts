import {
  useNavigate,
  useSearchParams,
  useParams,
} from 'react-router-dom';
import {
  useSavingsSettingsStore,
  type SavingsSettingsStep,
} from '@/features/savings/store/useSavingsSettingsStore';
import { PAGE_PATH, createSavingsDetailPath } from '@/shared/constants/path';

// 적금 설정 변경 단계 순서
const SAVINGS_SETTINGS_STEPS: SavingsSettingsStep[] = [
  'CURRENT_INFO',
  'SELECT_CHANGE',
  'NEW_SETTINGS',
  'CONFIRM',
  'COMPLETE',
];

/**
 * 적금 설정 변경 funnel의 단계 진행을 관리하는 커스텀 훅
 * URL 파라미터 기반으로 현재 단계를 결정하고 단계 간 이동을 처리합니다.
 *
 * @returns {object} 단계 진행 관련 상태와 함수들
 */
export const useSavingsSettingsChange = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { accountId } = useParams<{ accountId: string }>();
  const { setStep } = useSavingsSettingsStore();

  // URL 파라미터에서 from 값을 읽어옴
  const fromParam = searchParams.get('from');
  const entryPoint = fromParam ? decodeURIComponent(fromParam) : PAGE_PATH.HOME;

  // 현재 URL 파라미터에서 step 결정
  const currentStepFromUrl =
    (searchParams.get('step') as SavingsSettingsStep) || 'CURRENT_INFO';

  // 현재 단계의 인덱스
  const currentIndex = SAVINGS_SETTINGS_STEPS.findIndex(
    s => s === currentStepFromUrl,
  );
  const totalSteps = SAVINGS_SETTINGS_STEPS.length;

  /**
   * 이전 단계로 이동
   */
  const goToPreviousStep = () => {
    if (currentIndex > 0 && accountId) {
      const prevStep = SAVINGS_SETTINGS_STEPS[currentIndex - 1];
      if (prevStep) {
        const params = new URLSearchParams();
        params.set('step', prevStep);
        if (fromParam) {
          params.set('from', fromParam);
        }
        setStep(prevStep);
        navigate(
          `${PAGE_PATH.SAVINGS_SETTINGS_WITH_ID.replace(':accountId', accountId)}?${params.toString()}`,
        );
      }
    }
  };

  /**
   * 다음 단계로 이동
   */
  const goToNextStep = () => {
    if (currentIndex < SAVINGS_SETTINGS_STEPS.length - 1 && accountId) {
      const nextStep = SAVINGS_SETTINGS_STEPS[currentIndex + 1];
      if (nextStep) {
        const params = new URLSearchParams();
        params.set('step', nextStep);
        if (fromParam) {
          params.set('from', fromParam);
        }
        setStep(nextStep);
        navigate(
          `${PAGE_PATH.SAVINGS_SETTINGS_WITH_ID.replace(':accountId', accountId)}?${params.toString()}`,
        );
      }
    }
  };

  /**
   * 특정 단계로 직접 이동
   */
  const goToStep = (targetStep: SavingsSettingsStep) => {
    if (accountId) {
      const params = new URLSearchParams();
      params.set('step', targetStep);
      if (fromParam) {
        params.set('from', fromParam);
      }
      setStep(targetStep);
      navigate(
        `${PAGE_PATH.SAVINGS_SETTINGS_WITH_ID.replace(':accountId', accountId)}?${params.toString()}`,
      );
    }
  };

  /**
   * 적금 설정 변경을 중단하고 적금 상세 페이지로 돌아가기
   */
  const cancelAndGoBack = () => {
    if (accountId) {
      navigate(createSavingsDetailPath(accountId, entryPoint));
    }
  };

  return {
    currentStep: Math.max(currentIndex + 1, 1),
    totalSteps,
    canGoBack: currentIndex > 0,
    currentStepFromUrl,
    goToPreviousStep,
    goToNextStep,
    goToStep,
    cancelAndGoBack,
  };
};
