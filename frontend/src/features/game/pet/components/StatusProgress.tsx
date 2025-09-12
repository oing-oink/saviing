import { Progress } from '@/shared/components/ui/progress';
import { cn } from '@/lib/utils';

interface StatusProgressProps {
  label: string;
  value: number;
  maxValue: number;
  className?: string;
}

/**
 * 펫의 상태값을 프로그레스 바로 표시하는 컴포넌트
 *
 * 포만감, 애정도, 경험치 등의 상태를 시각적으로 표현
 */
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
