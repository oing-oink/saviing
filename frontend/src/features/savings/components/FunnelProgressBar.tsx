import { useLocation } from 'react-router-dom';
import { Progress } from '@/shared/components/ui/progress';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import { useSavingsTermination } from '@/features/savings/hooks/useSavingsTermination';

const FunnelProgressBar = () => {
  const location = useLocation();

  // 현재 경로에 따라 적절한 훅 선택
  const isSavingsSettings = location.pathname.includes('/settings');
  const isSavingsTermination = location.pathname.includes('/termination');

  const accountCreationProgress = useStepProgress();
  const savingsSettingsProgress = useSavingsSettingsChange();
  const savingsTerminationProgress = useSavingsTermination();

  const { currentStep, totalSteps } = isSavingsTermination
    ? savingsTerminationProgress
    : isSavingsSettings
    ? savingsSettingsProgress
    : accountCreationProgress;

  const percent = (currentStep / totalSteps) * 100;

  return (
    <div className="sticky top-0 z-50 w-full bg-white">
      <Progress value={percent} className="h-2 w-full rounded-none" />
    </div>
  );
};

export default FunnelProgressBar;
