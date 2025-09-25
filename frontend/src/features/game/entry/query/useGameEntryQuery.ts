import { useQuery } from '@tanstack/react-query';
import { getGameEntry } from '@/features/game/entry/api/gameEntryApi';
import type { GameEntryData } from '@/features/game/entry/types/gameEntryTypes';
import { gameEntryKeys } from '@/features/game/entry/query/gameEntryKeys';

/**
 * 홈 진입 시 서버에서 캐릭터/방/펫 정보를 가져오는 React Query 훅
 */
export const useGameEntryQuery = (options?: { enabled?: boolean }) => {
  return useQuery<GameEntryData, Error>({
    queryKey: gameEntryKeys.all,
    queryFn: getGameEntry,
    enabled: options?.enabled,
  });
};
