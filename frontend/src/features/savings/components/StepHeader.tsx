import { ChevronLeft } from 'lucide-react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import { PAGE_PATH } from '@/shared/constants/path';

const StepHeader = () => {
  const navigate = useNavigate();
  const location = useLocation();

  // 현재 경로에 따라 적절한 훅 선택
  const isSavingsSettings = location.pathname.includes('/settings');

  const accountCreationProgress = useStepProgress();
  const savingsSettingsProgress = useSavingsSettingsChange();

  const { currentStepFromUrl, goToPreviousStep, cancelAndGoBack } =
    isSavingsSettings
      ? savingsSettingsProgress
      : { ...accountCreationProgress, cancelAndGoBack: undefined };

  const handleBackClick = () => {
    if (isSavingsSettings) {
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
      if (currentStepFromUrl === 'START') {
        navigate(PAGE_PATH.WALLET);
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
