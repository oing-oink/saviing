import { ChevronLeft } from 'lucide-react';
<<<<<<< HEAD
import { useNavigate, useLocation } from 'react-router-dom';
=======
import { useNavigate, useSearchParams } from 'react-router-dom';
>>>>>>> 7dee7b84714a778a7f45647f0eedf63ec6ce8d48
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import { PAGE_PATH } from '@/shared/constants/path';

const StepHeader = () => {
  const navigate = useNavigate();
<<<<<<< HEAD
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
=======
  const [searchParams] = useSearchParams();
  const { currentStepFromUrl, goToPreviousStep } = useStepProgress();

  const handleBackClick = () => {
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
>>>>>>> 7dee7b84714a778a7f45647f0eedf63ec6ce8d48
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
