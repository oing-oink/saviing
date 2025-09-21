import { ChevronLeft } from 'lucide-react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { PAGE_PATH } from '@/shared/constants/path';

const StepHeader = () => {
  const navigate = useNavigate();
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
      }
    } else {
      goToPreviousStep();
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
