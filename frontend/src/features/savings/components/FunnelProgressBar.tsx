import { Progress } from '@/shared/components/ui/progress';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';

const FunnelProgressBar = () => {
  const { currentStep, totalSteps } = useStepProgress();
  const percent = (currentStep / totalSteps) * 100;

  return (
    <div className="sticky top-0 z-50 w-full bg-white">
      <Progress value={percent} className="h-2 w-full rounded-none" />
    </div>
  );
};

export default FunnelProgressBar;
