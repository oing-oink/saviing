import { ChevronLeft } from 'lucide-react';
import {
  useNavigate,
  useSearchParams,
  useLocation,
  useParams,
} from 'react-router-dom';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import { useSavingsTermination } from '@/features/savings/hooks/useSavingsTermination';
import { PAGE_PATH } from '@/shared/constants/path';

const StepHeader = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const { accountId } = useParams<{ accountId: string }>();

  // 현재 경로에 따라 적절한 훅 선택
  const isSavingsSettings = location.pathname.includes('/settings');
  const isSavingsTermination = location.pathname.includes('/termination');

  const accountCreationProgress = useStepProgress();
  const savingsSettingsProgress = useSavingsSettingsChange();
  const savingsTerminationProgress = useSavingsTermination();

  const getProgressData = () => {
    if (isSavingsTermination) {
      return {
        currentStepFromUrl: savingsTerminationProgress.currentStepParam,
        goToPreviousStep: savingsTerminationProgress.goToPrevStep,
        cancelAndGoBack: undefined,
      };
    } else if (isSavingsSettings) {
      return savingsSettingsProgress;
    } else {
      return { ...accountCreationProgress, cancelAndGoBack: undefined };
    }
  };

  const { currentStepFromUrl, goToPreviousStep, cancelAndGoBack } =
    getProgressData();

  const handleBackClick = () => {
    if (isSavingsTermination) {
      // 해지 플로우일 때
      if (currentStepFromUrl === 'WARNING') {
        // 첫 번째 단계에서는 적금 상세 페이지로 돌아가기
        if (accountId) {
          navigate(
            PAGE_PATH.SAVINGS_DETAIL_WITH_ID.replace(':accountId', accountId),
          );
        } else {
          navigate(PAGE_PATH.SAVINGS);
        }
      } else if (currentStepFromUrl === 'COMPLETE') {
        // 완료 단계에서는 홈으로 이동
        navigate(PAGE_PATH.HOME);
      } else {
        goToPreviousStep();
      }
    } else if (isSavingsSettings) {
      // 설정 변경 플로우일 때
      if (
        currentStepFromUrl === 'CURRENT_INFO' ||
        currentStepFromUrl === 'COMPLETE'
      ) {
        // 첫 번째 단계나 완료 단계에서는 적금 상세 페이지로 돌아가기
        cancelAndGoBack?.();
      } else {
        goToPreviousStep();
      }
    } else {
      // 계좌 개설 플로우일 때
      if (currentStepFromUrl === 'START') {
        // URL 파라미터에서 from 값 확인
        const fromParam = searchParams.get('from');

        if (fromParam === 'home') {
          navigate(PAGE_PATH.HOME);
        } else if (fromParam === 'wallet') {
          navigate(PAGE_PATH.WALLET);
        } else if (fromParam === 'products') {
          navigate(PAGE_PATH.PRODUCTS);
        } else {
          // 기본값: WalletPage로 이동 (하위호환성)
          navigate(PAGE_PATH.WALLET);
        }
      } else {
        goToPreviousStep();
      }
    }
  };

  return (
    <div className="border-b px-6 py-4">
      <div className="flex items-center justify-start">
        <button
          onClick={handleBackClick}
          className="flex items-center text-gray-600 hover:text-gray-900"
        >
          <ChevronLeft className="h-5 w-5" />
        </button>
      </div>
    </div>
  );
};

export default StepHeader;
