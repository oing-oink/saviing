import { Progress } from '@/shared/components/ui/progress';

interface StatProps {
  label: string;
  value: number;
  max: number;
}

const PetStat = ({ label, value, max }: StatProps) => {
  const percent = (value / max) * 100;

  return (
    <div className="flex flex-col items-center gap-0.5">
      {/* 라벨 */}
      <span className="text-[10px] font-semibold text-indigo-500">{label}</span>

      {/* 값 */}
      <span className="text-xs font-bold text-gray-800">
        {value}/{max}
      </span>

      {/* Progress bar */}
      <Progress value={percent} className="h-1 w-16 [&>div]:bg-green-400" />
    </div>
  );
};

export default PetStat;