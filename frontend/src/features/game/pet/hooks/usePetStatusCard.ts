import { usePetQuery } from '@/features/game/pet/query/usepetQuery';

const levelBadgeMap = {
  1: 'level-label-01',
  2: 'level-label-02',
  3: 'level-label-03',
  4: 'level-label-04',
  5: 'level-label-05',
  6: 'level-label-06',
  7: 'level-label-07',
  8: 'level-label-08',
  9: 'level-label-09',
  10: 'level-label-10',
} as const;

type LevelBadgeClass = (typeof levelBadgeMap)[keyof typeof levelBadgeMap];

export const usePetStatusCard = (petId: number) => {
  const { data, isLoading, error } = usePetQuery(petId);

  const safeLevel = Math.min(
    Math.max(data?.level ?? 1, 1),
    10,
  ) as keyof typeof levelBadgeMap;
  const levelClass: LevelBadgeClass = levelBadgeMap[safeLevel];

  return {
    petData: data,
    isLoading,
    error,
    levelClass,
  };
};
