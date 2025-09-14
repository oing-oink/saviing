import { Progress } from '@/shared/components/ui/progress';
import { cn } from '@/lib/utils';

interface StatusProgressProps {
  /** 진행바 라벨 텍스트 (예: '포만감', '애정도', '경험치') */
  label: string;
  /** 현재 값 */
  value: number;
  /** 최대 값 */
  maxValue: number;
  /** 추가적인 CSS 클래스명 */
  className?: string;
}

/**
 * 펫의 상태값을 프로그레스 바로 표시하는 컴포넌트
 *
 * 현재 값과 최대 값을 받아 백분율로 계산하여 진행바를 렌더링합니다.
 * 포만감, 애정도, 경험치 등 다양한 상태를 시각적으로 표현할 수 있습니다.
 *
 * @param props - 진행바 컴포넌트의 props
 * @param props.label - 진행바 라벨 텍스트
 * @param props.value - 현재 값
 * @param props.maxValue - 최대 값
 * @param props.className - 추가적인 CSS 클래스명
 * @returns 라벨과 진행바가 포함된 div 엘리먼트
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
