import { ChevronLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { PAGE_PATH } from '@/shared/constants/path';

const StepHeader = () => {
  const navigate = useNavigate();
  const { step } = useAccountCreationStore();
  const { goToPreviousStep } = useStepProgress();

  const handleBackClick = () => {
    if (step === 'START') {
      navigate(PAGE_PATH.WALLET);
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
