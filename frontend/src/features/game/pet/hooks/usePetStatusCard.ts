import { usePetQuery } from '@/features/game/pet/query/usepetQuery';

/**
 * 펫 레벨별 배지 CSS 클래스 매핑
 * 레벨 1-10에 해당하는 배지 스타일 클래스를 정의
 */
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

/**
 * 펫 상태 카드에서 사용하는 데이터와 UI 상태를 관리하는 훅
 *
 * @param petId - 조회할 펫의 ID
 * @returns 펫 데이터, 로딩 상태, 에러 상태, 레벨 배지 클래스를 포함한 객체
 */
export const usePetStatusCard = (petId: number) => {
  const { data, isLoading, error } = usePetQuery(petId);

  // 레벨 값을 1-10 범위로 제한
  // 기본값은 1, 최대값은 10으로 클램핑
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
