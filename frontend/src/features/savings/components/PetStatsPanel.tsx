import PetStat from './PetStat';

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
