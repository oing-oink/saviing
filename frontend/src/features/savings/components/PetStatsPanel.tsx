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

interface PetStatsPanelProps {
  affection: number;
  maxAffection: number;
  hunger: number;
  maxHunger: number;
  exp: number;
  maxExp: number;
}

const PetStatsPanel = ({
  affection,
  maxAffection,
  hunger,
  maxHunger,
  exp,
  maxExp,
}: PetStatsPanelProps) => {
  return (
    <div className="flex max-w-md justify-center gap-x-8 rounded-xl bg-white px-6 py-4 shadow">
      <PetStat label="애정도" value={affection} max={maxAffection} />
      <PetStat label="배고픔" value={hunger} max={maxHunger} />
      <PetStat label="경험치" value={exp} max={maxExp} />
    </div>
  );
};

export default PetStatsPanel;
