import { Progress } from '@/shared/components/ui/progress';
import { cn } from '@/lib/utils';

interface StatusProgressProps {
  label: string;
  value: number;
  maxValue: number;
  className?: string;
}

const StatusProgress = ({
  label,
  value,
  maxValue,
  className,
}: StatusProgressProps) => {
  return (
    <div className="flex flex-1 items-center gap-2">
      <div className="text-sm">{label}</div>
      <Progress
        value={(value / maxValue) * 100}
        className={cn('flex-1', className)}
      >
        {value}/{maxValue}
      </Progress>
    </div>
  );
};

export default StatusProgress;
